package com.github.manolo8.simplecraft.modules.skill.tools;

import com.github.manolo8.simplecraft.modules.user.User;
import org.bukkit.entity.LivingEntity;

public interface GiveDamage {

    void onGiveDamage(User user, LivingEntity entity, DamageResult damageResult);
}
