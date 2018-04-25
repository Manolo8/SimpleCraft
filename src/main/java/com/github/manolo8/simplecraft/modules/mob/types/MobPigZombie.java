package com.github.manolo8.simplecraft.modules.mob.types;

import com.github.manolo8.simplecraft.modules.mob.Mob;
import com.github.manolo8.simplecraft.modules.user.User;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionType;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MobPigZombie extends Mob {

    public MobPigZombie(Random random) {
        super("§4Pig Zombie", random);
    }

    @Override
    protected Entity spawnMob(Location location) {
        PigZombie zombie = location.getWorld().spawn(location, PigZombie.class);

        zombie.setBaby(false);
        zombie.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(180);
        zombie.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0.45);
        zombie.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(15);
        zombie.setHealth(120);

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
    protected void attack(Entity damager, Player target) {
        if (random.nextInt(4) != 1) return;

        splashPotion(target.getLocation(), PotionType.WEAKNESS, false, false);
    }

    @Override
    protected void attacked(Entity target, Player damager) {
        super.attacked(target,damager);
        if (random.nextInt(4) != 1) return;

        Vector vector = damager.getLocation().getDirection().normalize().multiply(-1).add(new Vector(0, 0.6, 0));
        damager.setVelocity(vector);

        if (random.nextInt(5) != 1) return;
        damager.getWorld().strikeLightning(damager.getLocation());
    }

    @Override
    protected void playerKill(User killer) {
        killer.giveExp(50);
    }
}
