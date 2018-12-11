package com.github.manolo8.simplecraft.interfaces;

import com.github.manolo8.simplecraft.module.user.User;
import org.bukkit.event.entity.ProjectileHitEvent;

public interface ProjectileHit {

    void onProjectileHit(User user, ProjectileHitEvent event);

}
