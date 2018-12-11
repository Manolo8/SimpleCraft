package com.github.manolo8.simplecraft.module.portal;

import com.github.manolo8.simplecraft.core.world.provider.AreaProvider;
import com.github.manolo8.simplecraft.core.world.WorldInfo;

public class PortalProvider extends AreaProvider<Portal, PortalService> {

    public PortalProvider(WorldInfo info, PortalService service) {
        super(info, service);
    }
}
