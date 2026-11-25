package com.skk.mtgbot.autoconfigure;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "tg.bot")
@Data
public class TgBotProperties {
    private String token;
    private long mediaGroupFlushDelayMs = 1500;
}