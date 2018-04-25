package com.github.manolo8.simplecraft.modules.mob.types;

import com.github.manolo8.simplecraft.modules.mob.Mob;
import com.github.manolo8.simplecraft.modules.user.User;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.WitherSkeleton;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MobWitherSkeleton extends Mob {

    public MobWitherSkeleton(Random random) {
        super("whiterskeleton", random);
    }

    @Override
    protected Entity spawnMob(Location location) {
        WitherSkeleton skeleton = location.getWorld().spawn(location, WitherSkeleton.class);

        skeleton.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0.13);
        skeleton.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(10);
        skeleton.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(800);
        skeleton.setHealth(800);

        skeleton.setCanPickupItems(false);

        return skeleton;
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
        target.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 100, 2, true, true, Color.RED));
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
