package com.github.manolo8.simplecraft.core.world.provider;

import com.github.manolo8.simplecraft.core.service.ContainerService;
import com.github.manolo8.simplecraft.core.world.WorldInfo;
import com.github.manolo8.simplecraft.core.world.container.Container;
import com.github.manolo8.simplecraft.utils.location.SimpleArea;

import java.sql.SQLException;

public class AreaProvider<E extends Container, S extends ContainerService<E, ?>> extends Provider<E, S> {

    public AreaProvider(WorldInfo worldInfo, S service) {
        super(worldInfo, service);
    }

    public E create(SimpleArea area, String name) throws SQLException {
        E container = service.create(name, worldInfo, area.build());

        worldInfo.getContainer().addContainer(container);

        return container;
    }

    public void updateArea(E container, SimpleArea area) {
        if (container.isAttached()) container.unAttachAndRemove();

        container.setArea(area.build());

        worldInfo.getContainer().addContainer(container);
    }

    public boolean canAdd(SimpleArea area) {
        return canAdd(area, null);
    }

    public boolean canUpdate(E container, SimpleArea area) {

        if (container.getWorldInfo() == worldInfo) {
            return canAdd(area, container);
        }

        return false;
    }

    private boolean canAdd(SimpleArea area, E ignore) {
        return worldInfo.getContainer().isAvailable(area.build(), ignore);
    }
}
