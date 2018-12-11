package com.github.manolo8.simplecraft.core.world.provider;

import com.github.manolo8.simplecraft.core.service.ContainerService;
import com.github.manolo8.simplecraft.core.service.RepositoryService;
import com.github.manolo8.simplecraft.core.world.WorldInfo;
import com.github.manolo8.simplecraft.core.world.container.Container;

import java.sql.SQLException;

public class Provider<E extends Container, S extends ContainerService<E, ?>> {

    protected final WorldInfo worldInfo;
    protected final S service;

    public Provider(WorldInfo worldInfo, S service) {
        this.worldInfo = worldInfo;
        this.service = service;
    }

    public boolean exists(String name) throws SQLException {
        return service.exists(name);
    }

    public E findByName(String name) throws SQLException {
        return service.findByName(name);
    }

    public boolean match(RepositoryService other) {
        return service == other;
    }
}
