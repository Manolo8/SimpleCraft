package com.github.manolo8.simplecraft.interfaces;

import com.github.manolo8.simplecraft.module.user.User;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

public interface EntityDamage {

    void onEntityReceiveDamage(User user, LivingEntity entity);
}
