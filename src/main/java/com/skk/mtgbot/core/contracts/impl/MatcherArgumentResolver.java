package com.skk.mtgbot.core.contracts.impl;

import com.pengrad.telegrambot.model.Update;
import com.skk.mtgbot.core.BotHandlerMapping;
import com.skk.mtgbot.core.MethodParameter;
import com.skk.mtgbot.core.contracts.BotArgumentResolver;

import java.util.regex.Matcher;

public class MatcherArgumentResolver implements BotArgumentResolver {

    @Override
    public int getOrder() {
        return 100;
    } // 比 String 低优先级

    @Override
    public boolean supports(MethodParameter parameter) {
        return Matcher.class.isAssignableFrom(parameter.getParameterType());
    }

    @Override
    public Object resolve(Update update, MethodParameter parameter, BotHandlerMapping mapping) {
        String text = mapping.resolveTextAsString(update);
        if (text != null) {
            Matcher matcher = mapping.getPattern().matcher(text);
            return matcher.matches() ? matcher : null;
        }
        return null;
    }
}