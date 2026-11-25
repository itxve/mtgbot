package com.skk.mtgbot.core.contracts;

public interface BotArgumentResolverConfigurer {

    /**
     * 用户在这里注册自己的 resolver
     * 内置 resolver 已经注册完毕，用户可以在此追加或覆盖
     */
    void configure(BotArgumentResolverRegistry registry);
}