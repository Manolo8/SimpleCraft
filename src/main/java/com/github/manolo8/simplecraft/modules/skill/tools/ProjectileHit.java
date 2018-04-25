package com.github.manolo8.simplecraft.modules.skill.tools;

import com.github.manolo8.simplecraft.modules.user.User;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;

public interface ProjectileHit {

    void onProjectileHit(User user, Projectile projectile);
}
