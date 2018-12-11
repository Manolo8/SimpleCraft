package com.github.manolo8.simplecraft.module.region;

import com.github.manolo8.simplecraft.core.world.provider.AreaProvider;
import com.github.manolo8.simplecraft.core.world.WorldInfo;

public class RegionProvider extends AreaProvider<Region, RegionService> {

    public RegionProvider(WorldInfo info, RegionService service) {
        super(info, service);
    }
}
