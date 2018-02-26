package com.github.manolo8.simplecraft.domain.region;

import com.github.manolo8.simplecraft.core.protection.Protection;
import com.github.manolo8.simplecraft.core.protection.ProtectionChecker;
import org.bukkit.Location;

public class RegionChecker implements ProtectionChecker {

    private final IWorldRegion iWorldRegion;

    public RegionChecker(IWorldRegion iWorldRegion) {
        this.iWorldRegion = iWorldRegion;
    }

    @Override
    public Protection getLocationProtection(Location location) {
        for (Region region : iWorldRegion.getRegions()) {
            if (region.isInArea(location)) return region;
        }

        return iWorldRegion.getDefaultProtection();
    }
}
