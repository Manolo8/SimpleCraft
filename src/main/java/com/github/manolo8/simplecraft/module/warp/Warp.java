package com.github.manolo8.simplecraft.module.warp;

import com.github.manolo8.simplecraft.core.world.WorldInfo;
import com.github.manolo8.simplecraft.core.data.model.named.NamedEntity;
import com.github.manolo8.simplecraft.interfaces.Teleportable;
import com.github.manolo8.simplecraft.module.user.User;
import com.github.manolo8.simplecraft.utils.location.SimpleLocation;
import org.bukkit.Location;
import org.bukkit.Material;

public class Warp extends NamedEntity implements Teleportable {

    private WorldInfo worldInfo;
    private SimpleLocation location;

    private int minRank;
    private int slot;
    private Material icon;


    //======================================================
    //=====================ENCAPSULATION====================
    //======================================================
    public WorldInfo getWorldInfo() {
        return worldInfo;
    }

    public void setWorldInfo(WorldInfo worldInfo) {
        this.worldInfo = worldInfo;
    }

    public SimpleLocation getLocation() {
        return location;
    }

    public void setLocation(SimpleLocation location) {
        this.location = location;
        modified();
    }

    public int getMinRank() {
        return minRank;
    }

    public void setMinRank(int minRank) {
        this.minRank = minRank;
        modified();
    }

    public int getSlot() {
        return slot;
    }

    public void setSlot(int slot) {
        this.slot = slot;
        modified();
    }

    public Material getIcon() {
        return icon;
    }

    public void setIcon(Material icon) {
        this.icon = icon;
        modified();
    }
    //======================================================
    //====================_ENCAPSULATION====================
    //======================================================


    //======================================================
    //========================METHODS=======================
    //======================================================
    @Override
    public boolean teleport(User user) {
        return user.teleport(location.getOf(worldInfo.getWorld()).add(0.5, 0, 0.5));
    }

    public Location asLocation() {
        return location.getOf(getWorldInfo().getWorld()).add(0.5, 0, 0.5);
    }
    //======================================================
    //========================METHODS=======================
    //======================================================
}