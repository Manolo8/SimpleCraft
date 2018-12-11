package com.github.manolo8.simplecraft.utils.entity;

import com.github.manolo8.simplecraft.core.data.model.base.BaseEntity;

import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.sql.SQLException;

public class LazyLoaderSingle<E extends BaseEntity> {

    private Reference<E> object;
    private LazySingle<E> lazySingle;

    public LazyLoaderSingle(LazySingle<E> lazySingle) {
        this.lazySingle = lazySingle;
    }

    public E get() {
        if (object == null || object.get() == null) load();
        return object.get();
    }

    public void set(E entity) {
        object = new SoftReference<>(entity);
    }

    private void load() {
        try {
            object = new SoftReference<>(lazySingle.load());
        } catch (SQLException e) {
            e.printStackTrace();
            object = null;
        }
    }

    public interface LazySingle<E extends BaseEntity> {

        E load() throws SQLException;
    }

}
