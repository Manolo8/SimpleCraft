package com.github.manolo8.simplecraft.module.skill.tools;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;

public interface Damage {

    default void onFall(DamageResult result) {
    }

    default void onGive(LivingEntity target, DamageResult damageResult) {
    }

    default void onReceive(LivingEntity target, DamageResult damageResult) {
    }

    default void onProjectileHit(Projectile projectile) {
    }
}
