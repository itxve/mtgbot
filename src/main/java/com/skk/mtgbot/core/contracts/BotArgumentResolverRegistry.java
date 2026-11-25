package com.skk.mtgbot.core.contracts;

import com.skk.mtgbot.core.MethodParameter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class BotArgumentResolverRegistry {

    private final List<BotArgumentResolver> resolvers = new ArrayList<>();

    /**
     * 注册一个 resolver（内置和用户共用这个方法）
     */
    public void add(BotArgumentResolver resolver) {
        resolvers.add(resolver);
    }

    /**
     * 按 order 排序后查找
     */
    public BotArgumentResolver findResolver(MethodParameter parameter) {
        // 懒排序：第一次查找时排一次
        // 或者提供 sort() 方法让外部在注册完毕后调用
        for (BotArgumentResolver r : resolvers) {
            if (r.supports(parameter)) {
                return r;
            }
        }
        return null;
    }

    /**
     * 注册完毕后调用，按 order 排序
     */
    public void sort() {
        resolvers.sort(Comparator.comparingInt(BotArgumentResolver::getOrder));
    }

    public List<BotArgumentResolver> all() {
        return Collections.unmodifiableList(resolvers);
    }
}