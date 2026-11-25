package com.skk.mtgbot.core;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

public class MethodParameter {

    private final Method method;

    private final int parameterIndex;
    // 缓存字段
    private final Class<?> parameterType;
    private final Parameter parameter;
    private final Annotation[] annotations;

    public MethodParameter(Method method, int parameterIndex) {
        this.method = method;
        this.parameterIndex = parameterIndex;
        this.parameterType = method.getParameterTypes()[parameterIndex];
        this.parameter = method.getParameters()[parameterIndex];
        this.annotations = this.parameter.getAnnotations();
    }

    public Method getMethod() {
        return method;
    }

    public Class<?> getParameterType() {
        return parameterType;
    }  // 直接返回缓存

    public Parameter getParameter() {
        return parameter;
    }          // 直接返回缓存

    // 直接返回缓存
    @SuppressWarnings("unchecked")
    public <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
        for (Annotation ann : annotations) {
            if (annotationClass.isInstance(ann)) {
                return (T) ann;
            }
        }
        return null;
    }

    public Annotation[] getAnnotations() {
        return annotations.clone();
    }

    public boolean hasAnnotation(Class<? extends Annotation> annotationClass) {
        return getAnnotation(annotationClass) != null;
    }

    // 关键：正确实现 equals/hashCode 让 ConcurrentHashMap 缓存生效
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MethodParameter)) return false;
        MethodParameter that = (MethodParameter) o;
        return parameterIndex == that.parameterIndex && method.equals(that.method);
    }

    @Override
    public int hashCode() {
        return 31 * method.hashCode() + parameterIndex;
    }
}