package com.github.manolo8.simplecraft.module.mine;

import com.github.manolo8.simplecraft.core.world.provider.AreaProvider;
import com.github.manolo8.simplecraft.core.world.WorldInfo;

public class MineProvider extends AreaProvider<Mine, MineService> {

    public MineProvider(WorldInfo info, MineService service) {
        super(info, service);
    }
}
