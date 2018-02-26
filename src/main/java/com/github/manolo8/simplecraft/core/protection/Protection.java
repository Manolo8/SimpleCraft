package com.github.manolo8.simplecraft.core.protection;

import org.bukkit.Location;
import org.bukkit.Material;

public interface Protection extends UserProtection {

    boolean isGlobal();

    boolean canSpread(Material type);

    boolean canPistonWork(Location initiator);

    boolean canExplode();
}
