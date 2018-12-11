package com.github.manolo8.simplecraft.utils.entity;

import com.github.manolo8.simplecraft.core.data.model.base.BaseEntity;

import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class LazyLoaderList<E extends BaseEntity> {

    private Reference<List<E>> objects;
    private LazyList<E> lazy;

    public LazyLoaderList(LazyList<E> lazy) {
        this.lazy = lazy;
    }

    public List<E> get() {
        if (objects == null || objects.get() == null) load();
        return objects.get();
    }

    public void add(E entity) {
        get().add(entity);
    }

    public void remove(E entity) {
        get().remove(entity);
    }

    private void load() {
        try {
            objects = new SoftReference<>(lazy.load());
        } catch (SQLException e) {
            e.printStackTrace();
            objects = new SoftReference<>(new ArrayList<>());
        }
    }

    public interface LazyList<E extends BaseEntity> {

        List<E> load() throws SQLException;
    }
}
