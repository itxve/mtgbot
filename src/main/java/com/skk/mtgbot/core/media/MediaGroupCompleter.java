package com.skk.mtgbot.core.media;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.skk.mtgbot.autoconfigure.TgBotProperties;
import lombok.Setter;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Consumer;

@Component
public class MediaGroupCompleter {

    public MediaGroupCompleter(TgBotProperties tgBotProperties) {
        this.tgBotProperties = tgBotProperties;
    }

    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
        Thread t = new Thread(r, "media-group-flusher");
        t.setDaemon(true);
        return t;
    });

    // mediaGroupId -> buffer
    private final Map<String, MediaGroupBuffer> buffers = new ConcurrentHashMap<>();

    // 已处理过的 messageId，防止重复 dispatch（延迟到达的消息可能重复触发）
    private final Set<Integer> processedMessageIds = ConcurrentHashMap.newKeySet();

    // flush 后的回调——交给 Dispatcher
    @Setter
    private Consumer<BatchedMediaGroup> flushHandler;

    private TgBotProperties tgBotProperties;


    /**
     * 中间件入口——对齐 ptb 的 media_group_completer
     * 返回 true 表示已拦截（media group 消息，等待聚合）
     * 返回 false 表示非 media group，调用方继续正常 dispatch
     */
    public boolean intercept(Update update) {
        Message message = update.message();
        if (message == null) {
            return false;
        }

        String mediaGroupId = message.mediaGroupId();

        if (mediaGroupId == null) {
            return false; // 不是 media group，不拦截
        }

        // 去重：同一 message 不处理两次
        if (!processedMessageIds.add(message.messageId())) {
            return true; // 已处理过，拦截
        }

        buffers.computeIfAbsent(mediaGroupId, k -> new MediaGroupBuffer(
                mediaGroupId,
                this::onGroupComplete,
                scheduler,
                tgBotProperties.getMediaGroupFlushDelayMs()
        )).add(message);

        return true; // 拦截，等待聚合后 flush
    }

    private void onGroupComplete(List<Message> messages) {
        if (messages.isEmpty()) return;

        // 按 messageId 排序，确保顺序
        messages.sort(Comparator.comparingInt(Message::messageId));

        // 找到主消息（第一个有 caption 的，没有就用第一个）
        Message mainMessage = messages.stream()
                .filter(m -> m.caption() != null && !m.caption().isEmpty())
                .findFirst()
                .orElse(messages.get(0));

        // 从 buffers 中移除
        String groupId = messages.get(0).mediaGroupId();
        buffers.remove(groupId);

        // 清理 processedMessageIds（防止内存泄漏）
        messages.forEach(m -> processedMessageIds.remove(m.messageId()));

        // 回调给 Dispatcher
        if (flushHandler != null) {
            flushHandler.accept(new BatchedMediaGroup(mainMessage, messages));
        }
    }

    @PreDestroy
    public void shutdown() {
        scheduler.shutdown();
        buffers.clear();
        processedMessageIds.clear();
    }
}