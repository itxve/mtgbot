package com.skk.mtgbot.core.contracts;

import com.pengrad.telegrambot.model.Update;
import com.skk.mtgbot.core.MsgUpdateType;

public class BotDefaultCondition {

    public static String resolveText(MsgUpdateType updateType, Update update) {
        switch (updateType) {
            case MESSAGE:
                return update.message() != null ? update.message().text() : null;
            case MEDIA_GROUP:
                return update.message() != null ? update.message().caption() : null;
            case CALLBACK_QUERY:
                return update.callbackQuery() != null ? update.callbackQuery().data() : null;
            case INLINE_QUERY:
                return update.inlineQuery() != null ? update.inlineQuery().query() : null;
            case WEB_APP_DATA:
                return update.message() != null && update.message().webAppData() != null
                        ? update.message().webAppData().data() : null;
            default:
                return null;
        }
    }
}
