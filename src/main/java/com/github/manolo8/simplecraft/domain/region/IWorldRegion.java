package com.github.manolo8.simplecraft.domain.region;

import com.github.manolo8.simplecraft.core.protection.Protection;
import com.github.manolo8.simplecraft.core.protection.ProtectionChecker;
import com.github.manolo8.simplecraft.core.protection.impl.DefaultProtection;
import com.github.manolo8.simplecraft.core.world.IWorld;
import org.bukkit.World;

import java.util.List;

public class IWorldRegion implements IWorld {

    private World world;
    private List<Region> regions;
    private RegionChecker checker;
    private Protection defaultProtection;

    public IWorldRegion(List<Region> regions) {
        this.regions = regions;
        this.checker = new RegionChecker(this);
        this.defaultProtection = new DefaultProtection();
    }

    public List<Region> getRegions() {
        return regions;
    }

    public void addRegion(Region region) {
        this.regions.add(region);
    }

    public void removeRegion(String name) {
        regions.removeIf(region -> region.getName().equalsIgnoreCase(name));
    }

    public Protection getDefaultProtection() {
        return defaultProtection;
    }

    @Override
    public boolean match(World world) {
        return this.world.equals(world);
    }

    @Override
    public void setWorld(World world) {
        this.world = world;
    }

    @Override
    public ProtectionChecker getChecker() {
        return checker;
    }
}
