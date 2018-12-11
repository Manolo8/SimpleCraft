package com.github.manolo8.simplecraft.module.plot;

import com.github.manolo8.simplecraft.core.world.WorldInfo;
import com.github.manolo8.simplecraft.core.world.provider.Provider;
import com.github.manolo8.simplecraft.module.user.User;
import org.bukkit.Location;

import java.sql.SQLException;

public class PlotProvider extends Provider<Plot, PlotService> {

    public PlotProvider(WorldInfo info, PlotService service) {
        super(info, service);
    }

    public Plot autoClaim(User user, String name) throws SQLException {
        Plot plot = service.create(user, name, worldInfo.getId());

        if (plot == null) return null;

        worldInfo.getContainer().addContainer(plot);

        return plot;
    }

    public Plot claim(User user, String name) throws SQLException {
        Location loc = user.base().getLocation();

        Plot plot = service.create(user, name, worldInfo.getId(), loc.getBlockX() >> 6, loc.getBlockZ() >> 6);

        if (plot == null) return null;

        worldInfo.getContainer().addContainer(plot);

        return plot;
    }
}
