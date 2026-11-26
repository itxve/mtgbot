package com.skk.mtgbot.autoconfigure;

import com.pengrad.telegrambot.TelegramBot;
import com.skk.mtgbot.core.BotDispatcher;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(TgBotProperties.class)
@ConditionalOnProperty(prefix = "tg.bot", name = "token")
@ComponentScan("com.skk.mtgbot")
public class TelbotAutoConfiguration {

   
    @Bean
    public TelegramBot telegramBot(TgBotProperties properties) {
        return new TelegramBot.Builder(properties.getToken())
                .build();
    }

    @Bean
    public BotPollingRunner botPollingRunner(TelegramBot bot, BotDispatcher dispatcher) {
        return new BotPollingRunner(bot, dispatcher);
    }
}