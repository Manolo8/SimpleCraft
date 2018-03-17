package com.github.manolo8.simplecraft.core.protection;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

public interface Protection extends UserProtection {

    boolean isGlobal();

    boolean canSpread(Material type);

    boolean canPistonWork();

    boolean canExplode();
}
