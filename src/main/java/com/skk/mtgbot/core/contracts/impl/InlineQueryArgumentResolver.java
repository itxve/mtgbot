package com.skk.mtgbot.core.contracts.impl;

import com.pengrad.telegrambot.model.Update;
import com.skk.mtgbot.core.BotHandlerMapping;
import com.skk.mtgbot.core.MethodParameter;
import com.skk.mtgbot.core.contracts.BotArgumentResolver;

public class InlineQueryArgumentResolver implements BotArgumentResolver {

    @Override
    public boolean supports(MethodParameter parameter) {
        return com.pengrad.telegrambot.model.InlineQuery.class
                .isAssignableFrom(parameter.getParameterType());
    }

    @Override
    public Object resolve(Update update, MethodParameter parameter, BotHandlerMapping mapping) {
        return update.inlineQuery();
    }
}