package com.github.manolo8.simplecraft.core.protection;

import com.github.manolo8.simplecraft.modules.user.User;
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

    /**
     * @param user the user
     * @param location the location
     * @return an Protection - if does not exists
     * return an default protection which only
     * ops can change anything
     */
    default Protection getUserProtection(User user, Location location) {
        Protection protection = user.getProtection();

        //Se a proteção for global ou o jogador/bloco que o jogador quebrou
        //Não está na área da proteção, o sistema pega uma proteção do
        //Checker
        if (!protection.isInArea(location) || protection.isGlobal()) {
            protection = user.getCurrentChecker().getLocationProtection(location);
            //Atualiza a nova proteção do jogador
            user.setProtection(protection);
        }

        return protection;
    }
}
