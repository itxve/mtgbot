package com.skk.mtgbot.core;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.skk.mtgbot.core.contracts.BotArgumentResolver;
import com.skk.mtgbot.core.media.BatchedMediaGroup;
import com.skk.mtgbot.core.media.MediaGroupCompleter;
import com.skk.mtgbot.core.media.MediaGroupContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Component
@Slf4j
public class BotDispatcher {

    private final List<BotHandlerMapping> mappings = new ArrayList<>();

    private final MediaGroupCompleter mediaGroupCompleter;

    private final BotArgumentResolverInitializer resolverInitializer;

    public BotDispatcher(MediaGroupCompleter mediaGroupCompleter, BotArgumentResolverInitializer resolverInitializer) {
        this.mediaGroupCompleter = mediaGroupCompleter;
        this.resolverInitializer = resolverInitializer;
        this.mediaGroupCompleter.setFlushHandler(this::dispatchMediaGroup);
    }

    public void register(BotHandlerMapping mapping) {
        mappings.add(mapping);
    }

    @PostConstruct
    public void init() {
        mappings.sort(Comparator.comparingInt(BotHandlerMapping::getOrder));
    }


    /**
     * 外部调用入口（比如从 TelegramBot 的 onUpdate 回调进来）
     */
    public void onUpdate(Update update) {
        // 先过中间件
        boolean intercepted = mediaGroupCompleter.intercept(update);
        if (intercepted) {
            return; // media group 消息被拦截，等聚合后由 flushHandler 触发 dispatch
        }
        // 非 media group，直接 dispatch
        dispatch(update);
    }


    public void dispatch(Update update) {
        for (BotHandlerMapping mapping : mappings) {
            if (!mapping.chatSupports(update)) continue;
            if (!mapping.testCondition(update)) continue;
            if (!mapping.matchText(update)) continue;
            try {
                Object[] arg = resolveArguments(update, mapping);
                mapping.invoke((Object[]) arg);      // ✅ 展开数组
            } catch (Exception e) {
                log.error("BotDispatcher {} failed for update {}", mapping.getMethod().getName(), update.updateId(), e);
            }
        }
    }

    /**
     * media group 聚合完成后调用
     */
    private void dispatchMediaGroup(BatchedMediaGroup group) {
        // 构造一个"虚拟 Update"，用主消息包装
        Update virtualUpdate = createVirtualUpdate(group.getMainMessage());

        MediaGroupContext.set(group);
        try {
            dispatch(virtualUpdate);
        } finally {
            MediaGroupContext.clear();
        }
    }


    private Object[] resolveArguments(Update update, BotHandlerMapping mapping) {
        Method method = mapping.getMethod();
        Class<?>[] paramTypes = method.getParameterTypes();

        if (paramTypes.length == 0) return new Object[0];

        Object[] args = new Object[paramTypes.length];
        for (int i = 0; i < paramTypes.length; i++) {
            MethodParameter mp = new MethodParameter(method, i);
            BotArgumentResolver resolver = resolverInitializer.getRegistry().findResolver(mp);
            if (resolver != null) {
                args[i] = resolver.resolve(update, mp, mapping);
            } else {
                args[i] = null;
            }
        }
        return args;
    }

    private Update createVirtualUpdate(Message message) {
        // 用反射或你项目里现有的方式构造 Update
        // 否则用反射设置 message 字段
        try {
            Update update = new Update();
            java.lang.reflect.Field f = Update.class.getDeclaredField("message");
            f.setAccessible(true);
            f.set(update, message);
            return update;
        } catch (Exception e) {
            throw new RuntimeException("Failed to create virtual update", e);
        }
    }
}