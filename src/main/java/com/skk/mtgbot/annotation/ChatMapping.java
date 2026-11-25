package com.skk.mtgbot.annotation;

import com.skk.mtgbot.core.ChatType;
import com.skk.mtgbot.core.MsgUpdateType;
import com.skk.mtgbot.core.contracts.BotHandlerCondition;

import java.lang.annotation.*;

@Target({ElementType.ANNOTATION_TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ChatMapping {

    String value() default ".*";

    /**
     * 想要处理caption 请一定要使用 UpdateType.MEDIA_GROUP
     *
     * @return -
     */
    MsgUpdateType msgType() default MsgUpdateType.MESSAGE;

    ChatType[] chatTypes() default {ChatType.all};

    int order() default 0;

    // 新增：指定条件类名
    Class<? extends BotHandlerCondition> condition() default BotHandlerCondition.class;
}