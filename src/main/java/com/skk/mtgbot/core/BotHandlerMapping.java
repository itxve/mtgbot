package com.skk.mtgbot.core;

import com.pengrad.telegrambot.model.Update;
import com.skk.mtgbot.core.contracts.BotDefaultCondition;
import com.skk.mtgbot.core.contracts.BotHandlerCondition;
import lombok.Getter;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class BotHandlerMapping {

    @Getter
    private final Pattern pattern;
    @Getter
    private final MsgUpdateType updateType;
    @Getter
    private final Set<ChatType> chatTypes;
    @Getter
    private final int order;
    @Getter
    private final Method method;
    @Getter
    private final Object bean;
    @Getter
    private final BotHandlerCondition condition;

    public BotHandlerMapping(
            String regex,
            MsgUpdateType updateType,
            ChatType[] chatTypes,
            int order,
            Method method,
            Object bean,
            BotHandlerCondition condition
    ) {
        this.pattern = Pattern.compile(regex);
        this.updateType = updateType;
        this.chatTypes = Arrays.stream(chatTypes).collect(Collectors.toSet());
        this.order = order;
        this.method = method;
        this.bean = bean;
        this.condition = condition;
    }

    public boolean chatSupports(Update update) {
        ChatType ct = resolveChatType(update);
        return chatTypes.contains(ChatType.all) || chatTypes.contains(ct);
    }

    public boolean matchText(Update update) {
        String text = resolveTextAsString(update);
        if (text == null) return false;
        return pattern.matcher(text).matches();
    }


    public void invoke(Object... args) throws Exception {
        method.setAccessible(true);
        method.invoke(bean, args);
    }

    private ChatType resolveChatType(Update update) {
        if (update.message() != null) {
            return ChatType.valueOf(update.message().chat().type().name());
        }
        return ChatType.Private;
    }

    public String resolveTextAsString(Update update) {
        if (condition != null) {
            return condition.resolveText(update);
        }
        return BotDefaultCondition.resolveText(updateType, update);
    }


    // 新增判断
    public boolean testCondition(Update update) {
        return condition == null || condition.test(update, this);
    }
}