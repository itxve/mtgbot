package com.skk.mtgbot.core.media;

import com.pengrad.telegrambot.model.Message;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class MediaGroupBuffer {

    private final List<Message> messages = new ArrayList<>();
    private final String mediaGroupId;
    private final Consumer<List<Message>> onFlush;
    private ScheduledFuture<?> flushTask;
    private final ScheduledExecutorService scheduler;
    private final Long mediaGroupFlushDelayMs;

    public MediaGroupBuffer(String mediaGroupId, Consumer<List<Message>> onFlush, ScheduledExecutorService scheduler, Long mediaGroupFlushDelayMs) {
        this.mediaGroupId = mediaGroupId;
        this.onFlush = onFlush;
        this.scheduler = scheduler;
        this.mediaGroupFlushDelayMs = mediaGroupFlushDelayMs;
    }

    public synchronized void add(Message message) {
        messages.add(message);

        // 重置定时器
        if (flushTask != null) {
            flushTask.cancel(false);
        }

        flushTask = scheduler.schedule(this::flush, this.mediaGroupFlushDelayMs, TimeUnit.MILLISECONDS);
    }

    private synchronized void flush() {
        List<Message> snapshot = new ArrayList<>(messages);
        messages.clear();
        onFlush.accept(snapshot);
    }

    public synchronized int size() {
        return messages.size();
    }
}