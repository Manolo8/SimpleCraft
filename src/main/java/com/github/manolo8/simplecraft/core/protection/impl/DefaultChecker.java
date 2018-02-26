package com.github.manolo8.simplecraft.core.protection.impl;

import com.github.manolo8.simplecraft.core.protection.Protection;
import com.github.manolo8.simplecraft.core.protection.ProtectionChecker;
import org.bukkit.Location;

public class DefaultChecker implements ProtectionChecker {

    private Protection protection;

    public DefaultChecker() {
        this.protection = new DefaultProtection();
    }

    @Override
    public Protection getLocationProtection(Location location) {
        return protection;
    }
}
