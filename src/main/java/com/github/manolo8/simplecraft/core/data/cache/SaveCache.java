package com.github.manolo8.simplecraft.core.data.cache;

import com.github.manolo8.simplecraft.core.data.model.base.BaseEntity;
import com.github.manolo8.simplecraft.core.data.model.base.Repository;

import java.sql.SQLException;

public class SaveCache<E extends BaseEntity, R extends Repository<E>> extends Cache<E> {

    private R repository;

    public SaveCache(R repository) {
        super();
        this.repository = repository;
    }

    public void save(E entity) {
        try {

            if (entity.isRemoved()) {
                repository.delete(entity);
            } else {
                repository.save(entity);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}