package com.github.manolo8.simplecraft.core.protection;

import com.github.manolo8.simplecraft.core.protection.impl.DefaultChecker;
import com.github.manolo8.simplecraft.core.protection.impl.DefaultProtection;
import com.github.manolo8.simplecraft.core.world.WorldService;
import com.github.manolo8.simplecraft.domain.user.User;
import org.bukkit.Location;
import org.bukkit.block.Block;

public class ProtectionController {

    private final WorldService worldService;

    public ProtectionController(WorldService worldService) {
        this.worldService = worldService;
    }

    public void userJoin(User user) {
        user.setCurrentChecker(new DefaultChecker());
        user.setProtection(new DefaultProtection());
    }

    public void changeWorld(User user) {
        user.setCurrentChecker(worldService.getChecker(user.getBase().getWorld()));
    }

    public boolean breakBlock(User user, Block block) {
        return getUserProtection(user, block.getLocation()).canBreak(user, block);
    }

    public boolean placeBlock(User user, Block block) {
        return getUserProtection(user, block.getLocation()).canPlace(user, block);
    }

    public boolean isPvpOn(User user, Location location) {
        return getUserProtection(user, location).isPvpOn();
    }

    public boolean isAnimalPvpOn(User user, Location location) {
        return getUserProtection(user, location).isAnimalPvpOn();
    }

    private Protection getUserProtection(User user, Location location) {
        Protection protection = user.getProtection();

        //Se a proteção for global ou o jogador/bloco que o jogador quebrou
        //Não está na área da proteção, o sistema pega uma proteção do
        //Checker (O checker é por mundo :))
        if (!protection.isInArea(location)
                || protection.isGlobal())
            protection = user.getCurrentChecker().getLocationProtection(location);

        return protection;
    }
}
