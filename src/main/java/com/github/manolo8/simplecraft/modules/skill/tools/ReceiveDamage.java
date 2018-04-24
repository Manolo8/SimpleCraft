package com.github.manolo8.simplecraft.modules.skill.tools;

import com.github.manolo8.simplecraft.modules.user.User;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

public interface ReceiveDamage {

    void onReceiveDamage(LivingEntity entity, User user, DamageResult damageResult);
}
