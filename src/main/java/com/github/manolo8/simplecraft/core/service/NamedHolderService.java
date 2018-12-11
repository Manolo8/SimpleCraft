package com.github.manolo8.simplecraft.core.service;

import com.github.manolo8.simplecraft.core.data.model.named.NamedEntity;
import com.github.manolo8.simplecraft.core.data.model.named.NamedRepository;

import java.sql.SQLException;

public class NamedHolderService<E extends NamedEntity, R extends NamedRepository<E, ?, ?, ?, ?>> extends HolderService<E, R> {

    public NamedHolderService(R repository) {
        super(repository);
    }

    public E create(String name) throws SQLException {
        E entity = repository.create(name);

        load(entity);

        return entity;
    }

    public void remove(E entity) {
        unload(entity);
        entity.remove();
    }

    public boolean exists(String name) {
        return findByName(name) != null;
    }

    public E findByName(String name) {
        return repository.getCache().getIfMatchName(name.toLowerCase());
    }
}
