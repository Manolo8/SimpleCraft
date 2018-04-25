package com.github.manolo8.simplecraft.modules.mob.types;

import com.github.manolo8.simplecraft.modules.mob.Mob;
import com.github.manolo8.simplecraft.modules.user.User;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MobBabyZombie extends Mob {

    public MobBabyZombie(Random random) {
        super("§4BabyZombie", random);
    }

    @Override
    protected Entity spawnMob(Location location) {
        Zombie zombie = location.getWorld().spawn(location, Zombie.class);

        zombie.setBaby(true);
        zombie.setHealth(15);
        zombie.setCanPickupItems(false);

        return zombie;
    }

    @Override
    protected List<ItemStack> getDrops() {
        List<ItemStack> drops = new ArrayList<>();

        drops.add(new ItemStack(Material.ENDER_STONE));
        drops.add(new ItemStack(Material.WHEAT));

        return drops;
    }

    @Override
    protected void playerKill(User killer) {
        killer.giveExp(50);
    }
}
