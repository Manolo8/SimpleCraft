package com.github.manolo8.simplecraft.core.protection;

import com.github.manolo8.simplecraft.domain.user.User;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

public interface UserProtection {

    boolean isInArea(Location location);

    boolean canBreak(User user, Block block);

    boolean canPlace(User user, Block block);

    boolean canInteract(User user, Block block);

    boolean isPvpOn();

    boolean isAnimalPvpOn();
}
