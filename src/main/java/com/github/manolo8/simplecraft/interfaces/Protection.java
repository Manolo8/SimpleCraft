package com.github.manolo8.simplecraft.interfaces;

import com.github.manolo8.simplecraft.module.user.User;
import org.bukkit.Material;

public interface Protection {

    boolean canSpread(Material type);

    boolean canPistonWork();

    boolean canExplode();

    boolean canEnter(User user);

    boolean canExit(User user);

    boolean canBreak(User user, Material type);

    boolean canPlace(User user, Material type);

    boolean canInteract(User user, Material type);

    boolean canRemoveSpecials(User user);

    boolean canUseSkill(int type);

    boolean canFly();

    boolean isPvpOn();

    boolean isPveOn(User user);
}
