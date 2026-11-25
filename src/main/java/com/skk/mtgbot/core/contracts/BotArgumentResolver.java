package com.skk.mtgbot.core.contracts;

import com.pengrad.telegrambot.model.Update;
import com.skk.mtgbot.core.BotHandlerMapping;
import com.skk.mtgbot.core.MethodParameter;

public interface BotArgumentResolver {

    boolean supports(MethodParameter parameter);

    Object resolve(Update update, MethodParameter parameter, BotHandlerMapping mapping);

    default int getOrder() {
        return 0;
    }
}