package com.github.manolo8.simplecraft.cache;

import com.github.manolo8.simplecraft.data.model.NamedEntity;

public class NamedCache<T extends NamedEntity> extends Cache<T> {

    public NamedCache(Class clazz) {
        super(clazz);
    }

    public T getIfMatch(String name) {
        for (T t : getCached())
            if (t.match(name)) return t;
        return null;
    }

}
