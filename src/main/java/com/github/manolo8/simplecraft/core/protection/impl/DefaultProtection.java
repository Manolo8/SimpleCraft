package com.github.manolo8.simplecraft.core.protection.impl;

import com.github.manolo8.simplecraft.core.protection.Protection;
import com.github.manolo8.simplecraft.modules.user.User;
import org.bukkit.Location;
import org.bukkit.Material;

public class DefaultProtection implements Protection {

    private int users;

    @Override
    public int getUsers() {
        return users;
    }

    @Override
    public void addUser() {
        users++;
    }

    @Override
    public void removeUser() {
        users--;
    }

    @Override
    public boolean isGlobal() {
        return true;
    }

    @Override
    public boolean isAnimalPvpOn(User user) {
        return false;
    }

    @Override
    public boolean isInArea(Location location) {
        return true;
    }

    @Override
    public boolean canBreak(User user, Material material) {
        return user.hasPermission("build.in.default");
    }

    @Override
    public boolean canPlace(User user, Material material) {
        return user.hasPermission("place.in.default");
    }

    @Override
    public boolean isPvpOn() {
        return false;
    }

    @Override
    public boolean canInteract(User user, Material material) {
        return user.hasPermission("interact.in.default");
    }

    @Override
    public boolean canSpread(Material type) {
        return false;
    }

    @Override
    public boolean canPistonWork() {
        return false;
    }

    @Override
    public boolean canExplode() {
        return false;
    }
}