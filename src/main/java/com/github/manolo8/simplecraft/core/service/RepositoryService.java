package com.github.manolo8.simplecraft.core.service;

import com.github.manolo8.simplecraft.core.data.model.base.Repository;

public class RepositoryService<R extends Repository> extends Service {

    protected final R repository;

    public RepositoryService(R repository) {
        this.repository = repository;
    }

}
