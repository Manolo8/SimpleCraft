package com.github.manolo8.simplecraft.interfaces;

import com.github.manolo8.simplecraft.module.user.User;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public interface EntityDeath {

    void onEntityDeath(User user, Entity entity, List<ItemStack> drops);
}
