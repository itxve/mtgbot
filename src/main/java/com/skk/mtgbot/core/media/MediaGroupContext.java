package com.skk.mtgbot.core.media;

import org.springframework.stereotype.Component;

@Component
public class MediaGroupContext {

    private static final ThreadLocal<BatchedMediaGroup> HOLDER = new ThreadLocal<>();

    public static void set(BatchedMediaGroup group) {
        HOLDER.set(group);
    }

    public static BatchedMediaGroup get() {
        return HOLDER.get();
    }

    public static void clear() {
        HOLDER.remove();
    }
}