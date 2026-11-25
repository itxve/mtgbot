package com.skk.mtgbot.scan;

import com.skk.mtgbot.annotation.BotController;
import com.skk.mtgbot.annotation.ChatMapping;
import com.skk.mtgbot.core.BotDispatcher;
import com.skk.mtgbot.core.BotHandlerMapping;
import com.skk.mtgbot.core.contracts.BotHandlerCondition;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.lang.reflect.Method;

@Component
@Slf4j
public class BotControllerScanner implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Autowired
    private BotDispatcher dispatcher;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @PostConstruct
    public void scan() {
        String[] beanNames = applicationContext.getBeanNamesForAnnotation(BotController.class);
        for (String beanName : beanNames) {
            Object bean = applicationContext.getBean(beanName);
            Class<?> clazz = bean.getClass();

            for (Method method : clazz.getDeclaredMethods()) {
                ChatMapping mapping = method.getAnnotation(ChatMapping.class);
                if (mapping == null) continue;
                BotHandlerCondition condition = null;
                Class<? extends BotHandlerCondition> conditionClass = mapping.condition();
                if (conditionClass != BotHandlerCondition.class) {
                    try {
                        condition = conditionClass.newInstance();
                    } catch (Exception e) {
                        log.error("condition newInstance error : method name {}", method.getName());
                    }
                }
                dispatcher.register(new BotHandlerMapping(
                        mapping.value(),
                        mapping.msgType(),
                        mapping.chatTypes(),
                        mapping.order(),
                        method,
                        bean,
                        condition
                ));
            }
        }
    }
}