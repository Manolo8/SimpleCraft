package com.github.manolo8.simplecraft.core.protection;

import com.github.manolo8.simplecraft.modules.user.User;
import org.bukkit.Location;
import org.bukkit.Material;

public interface UserProtection {

    boolean isInArea(Location location);

    boolean canBreak(User user, Material type);

    boolean canPlace(User user, Material type);

    boolean canInteract(User user, Material type);

    boolean isPvpOn();

    boolean isAnimalPvpOn(User user);
}
