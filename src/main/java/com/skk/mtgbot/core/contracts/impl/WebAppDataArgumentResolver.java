package com.skk.mtgbot.core.contracts.impl;

import com.pengrad.telegrambot.model.Update;
import com.skk.mtgbot.core.BotHandlerMapping;
import com.skk.mtgbot.core.MethodParameter;
import com.skk.mtgbot.core.contracts.BotArgumentResolver;

public class WebAppDataArgumentResolver implements BotArgumentResolver {

    @Override
    public boolean supports(MethodParameter parameter) {
        return com.pengrad.telegrambot.model.WebAppData.class
                .isAssignableFrom(parameter.getParameterType());
    }

    @Override
    public Object resolve(Update update, MethodParameter parameter, BotHandlerMapping mapping) {
        return update.message() != null ? update.message().webAppData() : null;
    }
}