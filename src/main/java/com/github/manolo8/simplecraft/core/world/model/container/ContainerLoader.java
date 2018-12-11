package com.github.manolo8.simplecraft.core.world.model.container;

import com.github.manolo8.simplecraft.core.world.WorldInfoRepository;
import com.github.manolo8.simplecraft.core.world.container.Area;
import com.github.manolo8.simplecraft.core.world.container.Container;
import com.github.manolo8.simplecraft.core.data.model.named.NamedLoader;

import java.sql.SQLException;

public abstract class ContainerLoader<E extends Container, O extends ContainerDTO>
        extends NamedLoader<E, O> {

    private final WorldInfoRepository worldInfoRepository;

    protected ContainerLoader(WorldInfoRepository worldInfoRepository) {
        this.worldInfoRepository = worldInfoRepository;
    }

    @Override
    public E fromDTO(O dto) throws SQLException {
        E entity = super.fromDTO(dto);

        entity.setWorldInfo(worldInfoRepository.findOne(dto.worldId));

        entity.setArea(new Area(dto.maxX,
                dto.maxY,
                dto.maxZ,
                dto.minX,
                dto.minY,
                dto.minZ
        ));

        return entity;
    }

    @Override
    public O toDTO(E entity) throws SQLException {
        O dto = super.toDTO(entity);

        Area area = entity.getArea();

        dto.worldId = entity.getWorldInfo().getId();
        dto.load(area);

        return dto;
    }
}
