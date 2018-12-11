package com.github.manolo8.simplecraft.core.placeholder;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public interface PlaceHolderBuilder<T> {

    PlaceHolder build(T target);

    default Class getTargetClass() {
        ParameterizedType parameterizedType = (ParameterizedType) getClass().getGenericInterfaces()[0];
        Type[] typeArguments = parameterizedType.getActualTypeArguments();

        return (Class<?>) typeArguments[0];
    }
}
