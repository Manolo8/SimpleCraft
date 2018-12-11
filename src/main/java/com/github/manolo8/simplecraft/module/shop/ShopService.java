package com.github.manolo8.simplecraft.module.shop;

import com.github.manolo8.simplecraft.core.service.ContainerService;
import com.github.manolo8.simplecraft.core.world.WorldInfo;
import com.github.manolo8.simplecraft.core.world.container.Area;
import com.github.manolo8.simplecraft.core.world.container.Container;
import com.github.manolo8.simplecraft.utils.def.Matcher;

import java.sql.SQLException;

public class ShopService extends ContainerService<Shop, ShopRepository> {

    public ShopService(ShopRepository repository) {
        super(repository, -1);
    }

    @Override
    public ShopProvider initProvider(WorldInfo worldInfo) {
        return new ShopProvider(worldInfo, this);
    }

    @Override
    public Matcher<Container> matcher() {
        return entity -> entity.getClass() == Shop.class;
    }

    public Shop create(ShopConverter converter, Area area) throws SQLException {
        return repository.create(converter, area);
    }
}
