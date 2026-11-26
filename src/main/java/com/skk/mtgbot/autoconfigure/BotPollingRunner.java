package com.skk.mtgbot.autoconfigure;


import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.utility.BotUtils;
import com.skk.mtgbot.core.BotDispatcher;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;

@Slf4j
public class BotPollingRunner {

    private final TelegramBot bot;
    private final BotDispatcher dispatcher;

    public BotPollingRunner(TelegramBot bot, BotDispatcher dispatcher) {
        this.bot = bot;
        this.dispatcher = dispatcher;
    }

    @PostConstruct
    public void run() {
        log.info("BotPollingRunner Listening......");
        bot.setUpdatesListener(updates -> {
            for (Update update : updates) {
                try {
                    log.info("Update :{}", BotUtils.toJson(update));
                    dispatcher.onUpdate(update);
                } catch (Exception e) {
                    log.info("setUpdatesListener Exception :{0}", e);
                }
            }
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        });
    }
}