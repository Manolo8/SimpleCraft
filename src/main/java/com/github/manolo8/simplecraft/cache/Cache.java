package com.github.manolo8.simplecraft.cache;

import com.github.manolo8.simplecraft.exception.CacheReferenceWrong;
import com.github.manolo8.simplecraft.model.BaseEntity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Cache<T extends BaseEntity> {

    private Class clazz;
    private List<T> cached;

    public Cache(Class clazz) {
        this.cached = new ArrayList<>();
        this.clazz = clazz;
    }

    public boolean isSame(Class clazz) {
        return this.clazz.equals(clazz);
    }

    public T getIfMatch(Integer id) {
        for (T t : cached)
            if (t.match(id)) {
                t.setLastCheck(System.currentTimeMillis());
                return t;
            }
        return null;
    }

    public void add(T t) {
        if (t.getReferences() != null) throw new CacheReferenceWrong();
        t.setReferences(1);
        t.setLastCheck(System.currentTimeMillis());
        cached.add(t);
    }

    public List<T> getCached() {
        return cached;
    }

    public void remove(BaseEntity baseEntity) {
        Iterator<T> i = cached.iterator();

        while (i.hasNext()) {
            if (i.next() == baseEntity) {
                i.remove();
                break;
            }
        }
    }
}
