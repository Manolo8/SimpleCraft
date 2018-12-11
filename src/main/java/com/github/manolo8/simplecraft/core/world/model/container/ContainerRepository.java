package com.github.manolo8.simplecraft.core.world.model.container;

import com.github.manolo8.simplecraft.core.world.WorldInfo;
import com.github.manolo8.simplecraft.core.world.container.Area;
import com.github.manolo8.simplecraft.core.world.container.ChunkContainer;
import com.github.manolo8.simplecraft.core.world.container.Container;
import com.github.manolo8.simplecraft.core.data.connection.Database;
import com.github.manolo8.simplecraft.core.data.model.named.NamedCache;
import com.github.manolo8.simplecraft.core.data.model.named.NamedRepository;

import java.sql.SQLException;
import java.util.List;

public abstract class ContainerRepository<E extends Container,
        O extends ContainerDTO,
        D extends ContainerDAO<O>,
        C extends NamedCache<E, ?>,
        L extends ContainerLoader<E, O>>
        extends NamedRepository<E, O, D, C, L> {

    public ContainerRepository(Database database) {
        super(database);
    }

    public List<E> findByContainer(ChunkContainer container, WorldInfo info) throws SQLException {
        return findByIdIn(dao.findByContainer(container, info.getId()));
    }

    public E create(String name, WorldInfo info, Area area) throws SQLException {
        O dto = loader.newDTO();

        dto.name = name;
        dto.fastName = name == null ? null : name.toLowerCase();
        dto.worldId = info.getId();
        dto.load(area);

        return create(dto);
    }
}
