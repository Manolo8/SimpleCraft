package com.github.manolo8.simplecraft.module.clan.clanarea;

import com.github.manolo8.simplecraft.core.world.provider.AreaProvider;
import com.github.manolo8.simplecraft.core.world.WorldInfo;

public class ClanAreaProvider extends AreaProvider<ClanArea, ClanAreaService> {

    public ClanAreaProvider(WorldInfo info, ClanAreaService service) {
        super(info, service);
    }
}
