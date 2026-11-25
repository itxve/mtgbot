package com.skk.mtgbot.core;

import com.skk.mtgbot.core.contracts.BotArgumentResolver;
import com.skk.mtgbot.core.contracts.BotArgumentResolverConfigurer;
import com.skk.mtgbot.core.contracts.BotArgumentResolverRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;

@Component
public class BotArgumentResolverInitializer implements ApplicationContextAware {

    private final BotArgumentResolverRegistry registry = new BotArgumentResolverRegistry();

    /**
     * 内置 Resolver SPI 化（可选，扩展性）
     * META-INF/services 自动注入
     */
    static List<BotArgumentResolver> builtinResolvers() {
        List<BotArgumentResolver> list = new ArrayList<>();
        ServiceLoader.load(BotArgumentResolver.class)
                .iterator()
                .forEachRemaining(list::add);
        return list;
    }

    @Override
    public void setApplicationContext(ApplicationContext context) {
        // 1. 注册内置
        for (BotArgumentResolver r : builtinResolvers()) {
            registry.add(r);
        }

        // 2. 收集用户写的 @Component resolver
        Map<String, BotArgumentResolver> springResolvers =
                context.getBeansOfType(BotArgumentResolver.class);
        for (BotArgumentResolver r : springResolvers.values()) {
            // 跳过内置 class（防止 Spring 扫到内置的，如果内置也标了 @Component 的话）
            registry.add(r);
        }

        // 3. 调用用户 Configurer
        Map<String, BotArgumentResolverConfigurer> configurers =
                context.getBeansOfType(BotArgumentResolverConfigurer.class);
        for (BotArgumentResolverConfigurer c : configurers.values()) {
            c.configure(registry);
        }

        // 4. 排序
        registry.sort();
    }

    public BotArgumentResolverRegistry getRegistry() {
        return registry;
    }
}