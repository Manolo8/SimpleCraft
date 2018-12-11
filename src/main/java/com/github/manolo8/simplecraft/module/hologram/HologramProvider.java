package com.github.manolo8.simplecraft.module.hologram;

import com.github.manolo8.simplecraft.core.world.WorldInfo;
import com.github.manolo8.simplecraft.core.world.provider.AreaProvider;

public class HologramProvider extends AreaProvider<Hologram, HologramService> {

    public HologramProvider(WorldInfo worldInfo, HologramService service) {
        super(worldInfo, service);
    }

}
