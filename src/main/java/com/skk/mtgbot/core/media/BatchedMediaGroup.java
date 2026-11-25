package com.skk.mtgbot.core.media;

import com.pengrad.telegrambot.model.Message;

import java.util.List;


public class BatchedMediaGroup {

    /**
     * 主消息（第一个有 caption 的，用于匹配正则/command）
     */
    private final Message mainMessage;

    public List<Message> getAllMessages() {
        return allMessages;
    }

    public Message getMainMessage() {
        return mainMessage;
    }

    /**
     * 该 group 的所有消息（已按 messageId 排序）
     */
    private final List<Message> allMessages;

    public BatchedMediaGroup(Message mainMessage, List<Message> allMessages) {
        this.mainMessage = mainMessage;
        this.allMessages = allMessages;
    }
}