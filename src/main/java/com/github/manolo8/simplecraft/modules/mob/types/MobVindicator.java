package com.github.manolo8.simplecraft.modules.mob.types;

import com.github.manolo8.simplecraft.modules.mob.Mob;
import com.github.manolo8.simplecraft.modules.user.User;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vindicator;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MobVindicator extends Mob {

    public MobVindicator(Random random) {
        super("§4Vindicator", random);
    }

    @Override
    protected Entity spawnMob(Location location) {
        Vindicator vindicator = location.getWorld().spawn(location, Vindicator.class);

        vindicator.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0.5);
        vindicator.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(15);
        vindicator.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(50);
        vindicator.setHealth(50);
        vindicator.setCanPickupItems(false);

        return vindicator;
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
        if (random.nextInt(2) != 1) return;
        splashPotion(target.getLocation(), PotionType.INSTANT_DAMAGE, false, true);
    }

    @Override
    protected void attacked(Entity target, Player damager) {
        super.attacked(target,damager);
        damager.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 100, 1, true, true, Color.RED));
    }

    @Override
    protected void playerKill(User killer) {
        killer.giveExp(50);
    }
}
