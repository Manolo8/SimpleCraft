package com.github.manolo8.simplecraft.core.world;

import com.github.manolo8.simplecraft.core.protection.ProtectionChecker;
import org.bukkit.World;

public interface IWorld {

    boolean match(World world);

    void setWorld(World world);

    ProtectionChecker getChecker();
}