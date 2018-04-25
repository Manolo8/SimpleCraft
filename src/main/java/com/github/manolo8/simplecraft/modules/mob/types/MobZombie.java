package com.github.manolo8.simplecraft.modules.mob.types;

import com.github.manolo8.simplecraft.modules.mob.Mob;
import com.github.manolo8.simplecraft.modules.user.User;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MobZombie extends Mob {

    public MobZombie(Random random) {
        super("zombie", random);
    }

    @Override
    protected Entity spawnMob(Location location) {
        Zombie zombie = location.getWorld().spawn(location, Zombie.class);

        zombie.setBaby(false);
        zombie.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(25);
        zombie.setHealth(25);
        zombie.setCanPickupItems(false);

        zombie.getHealth();
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
    protected void attacked(Entity target, Player damager) {
        super.attacked(target, damager);
    }

    @Override
    protected void playerKill(User killer) {
        killer.giveExp(50);
    }
}
