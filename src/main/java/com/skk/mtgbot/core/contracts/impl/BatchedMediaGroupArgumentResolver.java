package com.skk.mtgbot.core.contracts.impl;

import com.pengrad.telegrambot.model.Update;
import com.skk.mtgbot.core.BotHandlerMapping;
import com.skk.mtgbot.core.MethodParameter;
import com.skk.mtgbot.core.contracts.BotArgumentResolver;
import com.skk.mtgbot.core.media.BatchedMediaGroup;
import com.skk.mtgbot.core.media.MediaGroupContext;

public class BatchedMediaGroupArgumentResolver implements BotArgumentResolver {

    
    @Override
    public boolean supports(MethodParameter parameter) {
        return BatchedMediaGroup.class.isAssignableFrom(parameter.getParameterType());
    }

    @Override
    public Object resolve(Update update, MethodParameter parameter, BotHandlerMapping mapping) {
        return MediaGroupContext.get();
    }
}