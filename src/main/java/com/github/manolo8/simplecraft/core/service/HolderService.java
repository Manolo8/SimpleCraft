package com.github.manolo8.simplecraft.core.service;

import com.github.manolo8.simplecraft.core.data.model.base.BaseEntity;
import com.github.manolo8.simplecraft.core.data.model.base.Repository;

import java.util.ArrayList;
import java.util.List;

public class HolderService<E extends BaseEntity, R extends Repository<E>>
        extends RepositoryService<R> {

    protected final List<E> entities;

    public HolderService(R repository) {
        super(repository);
        this.entities = new ArrayList<>();
    }

    @Override
    public void init() throws Exception {
        super.init();

        for (E entity : repository.findAll()) {
            load(entity);
        }
    }

    //USAR SYNCHRONIZED!
    public List<E> getEntities() {
        return entities;
    }

    protected void load(E entity) {
        entities.add(entity);
    }

    protected void unload(E entity) {
        entities.remove(entity);
    }
}
