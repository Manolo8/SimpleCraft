package com.github.manolo8.simplecraft.modules.region;

import com.github.manolo8.simplecraft.core.protection.Protection;
import com.github.manolo8.simplecraft.core.protection.ProtectionChecker;
import com.github.manolo8.simplecraft.core.world.IWorld;
import com.github.manolo8.simplecraft.modules.user.User;
import org.bukkit.Chunk;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

public class IWorldRegion implements IWorld, ProtectionChecker {

    private int worldId;
    private List<Region> regions;
    private Region defaultRegion;

    public IWorldRegion(int worldId) {
        this.worldId = worldId;
        this.regions = new ArrayList<>();
        this.defaultRegion = new DefaultRegion();
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
        for (Region region : regions)
            if (region.isInArea(location)) {
                if (region.notHasSubRegions()) return region;
                else for (Region subRegion : region.getSubRegions())
                    if (subRegion.isInArea(location)) return subRegion;
                return region;
            }

        return defaultRegion;
    }

    @Override
    public Protection getUserProtection(User user, Location location) {
        Protection protection = (Region) user.getProtection();

        //Se a proteção for global ou o jogador/bloco que o jogador quebrou
        //Não está na área da proteção, o sistema pega uma proteção do
        //Checker
        if (!protection.isInArea(location) || protection.isGlobal()) {
            protection = getLocationProtection(location);
            //Atualiza a nova proteção do jogador
            user.setProtection(protection);
        }

        Region region = (Region) protection;

        if(region.notHasSubRegions()) return protection;

        for (Region subRegion : region.getSubRegions())
            if (subRegion.isInArea(location)) return subRegion;

        return protection;
    }
}
