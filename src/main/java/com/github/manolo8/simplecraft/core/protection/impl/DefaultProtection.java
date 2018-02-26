package com.github.manolo8.simplecraft.core.protection.impl;

import com.github.manolo8.simplecraft.core.protection.Protection;
import com.github.manolo8.simplecraft.domain.user.User;
import org.bukkit.Location;
import org.bukkit.Material;

public class DefaultProtection implements Protection {

    @Override
    public boolean isGlobal() {
        return true;
    }

    @Override
    public boolean isAnimalPvpOn() {
        return false;
    }

    @Override
    public boolean isInArea(Location location) {
        return true;
    }

    @Override
    public boolean canBreak(User user) {
        return user.hasPermission("build.in.default");
    }

    @Override
    public boolean canPlace(User user) {
        return user.hasPermission("place.in.default");
    }

    @Override
    public boolean isPvpOn() {
        return false;
    }

    @Override
    public boolean canInteract(User user, Material target) {
        return user.hasPermission("interact.in.default");
    }

    @Override
    public boolean canSpread(Material type) {
        return false;
    }

    @Override
    public boolean canPistonWork(Location initiator) {
        return false;
    }

    @Override
    public boolean canExplode() {
        return false;
    }
}