package com.github.manolo8.simplecraft.domain.region;

import com.github.manolo8.simplecraft.core.protection.Protection;
import com.github.manolo8.simplecraft.core.protection.ProtectionChecker;
import com.github.manolo8.simplecraft.core.protection.impl.DefaultProtection;
import com.github.manolo8.simplecraft.core.world.IWorld;
import org.bukkit.Chunk;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

public class IWorldRegion implements IWorld, ProtectionChecker {

    private int worldId;
    private List<Region> regions;
    private Protection defaultProtection;

    public IWorldRegion(int worldId) {
        this.worldId = worldId;
        this.regions = new ArrayList<>();
        this.defaultProtection = new DefaultProtection();
    }

    public List<Region> getRegions() {
        return regions;
    }

    public void addRegion(Region region) {
        this.regions.add(region);
        //Adiciona uma referencia para nao
        //Ser removida do sistema de cache
        // :)
        region.addReference();
    }

    public Protection getDefaultProtection() {
        return defaultProtection;
    }

    @Override
    public boolean match(int worldId) {
        return this.worldId == worldId;
    }

    @Override
    public void chunkLoad(int x, int z) {
        //Não vamos usar isso no região
    }

    @Override
    public void chunkUnload(int x, int z, Chunk[] chunks) {
        //Não vamos usar isso no região
    }

    @Override
    public ProtectionChecker getChecker() {
        return this;
    }

    @Override
    public Protection getLocationProtection(Location location) {
        for (Region region : regions) {
            if (region.isInArea(location)) return region;
        }

        return defaultProtection;
    }
}
