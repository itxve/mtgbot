package com.skk.mtgbot.core.contracts;

import com.pengrad.telegrambot.model.Update;
import com.skk.mtgbot.core.BotHandlerMapping;

@FunctionalInterface
public interface BotHandlerCondition {
    /**
     * 前置匹配条件
     *
     * @param update  -
     * @param mapping -
     * @return -
     */
    boolean test(Update update, BotHandlerMapping mapping);

    /**
     * 决定"正则匹配用哪段文本"
     * 默认返回 null → 走框架默认的 UpdateType 逻辑
     */
    default String resolveText(Update update) {
        return null;

    }
}