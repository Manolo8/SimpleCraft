package com.github.manolo8.simplecraft.core.protection;

import org.bukkit.Location;

/**
 * Each world will have a custom protection checker
 * if an world does not have, an default protectionChecker
 * will automatically return an 0 permission protection
 * for that player in all location
 */
public interface ProtectionChecker {

    /**
     * @param location location
     * @return an Protection - if does not exists
     * return an default protection which only
     * ops can change anything
     */
    Protection getLocationProtection(Location location);
}
