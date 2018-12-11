package com.github.manolo8.simplecraft.core.service;

import com.github.manolo8.simplecraft.core.world.WorldInfo;
import com.github.manolo8.simplecraft.core.world.container.Area;
import com.github.manolo8.simplecraft.core.world.container.ChunkContainer;
import com.github.manolo8.simplecraft.core.world.container.Container;
import com.github.manolo8.simplecraft.core.world.model.container.ContainerRepository;
import com.github.manolo8.simplecraft.core.world.provider.Provider;
import com.github.manolo8.simplecraft.utils.def.Matcher;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public abstract class ContainerService<E extends Container,
        R extends ContainerRepository<E, ?, ?, ?, ?>>
        extends RepositoryService<R> {

    private final int flagId;

    public ContainerService(R repository, int flagId) {
        super(repository);

        this.flagId = flagId;
    }

    public abstract Provider<E, ?> initProvider(WorldInfo worldInfo);

    public List<E> containerLoad(WorldInfo worldInfo, ChunkContainer container) {
        try {
            return repository.findByContainer(container, worldInfo);
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList();
        }
    }

    public E create(String name, WorldInfo info, Area area) throws SQLException {
        return repository.create(name, info, area);
    }

    public E findByName(String name) throws SQLException {
        return repository.findByName(name);
    }

    public boolean exists(String name) throws SQLException {
        return findByName(name) != null;
    }

    public List<E> findAll() throws SQLException {
        return repository.findAll();
    }

    public boolean useInWorld(WorldInfo worldInfo) {
        return flagId == -1 || worldInfo.flags().has(flagId);
    }

    public abstract Matcher<Container> matcher();
}
