package com.github.manolo8.simplecraft.core.protection;

import org.bukkit.Material;

public interface Protection extends UserProtection {

    int getUsers();

    void addUser();

    void removeUser();

    boolean isGlobal();

    boolean canSpread(Material type);

    boolean canPistonWork();

    boolean canExplode();
}
