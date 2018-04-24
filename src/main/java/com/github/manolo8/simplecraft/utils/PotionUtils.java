package com.github.manolo8.simplecraft.utils;

import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PotionUtils {

    public static void applyPotion(LivingEntity entity, PotionEffectType type, int duration, int amplifier) {
        PotionEffect effect = new PotionEffect(type, duration, amplifier);
        effect.apply(entity);
    }
}
