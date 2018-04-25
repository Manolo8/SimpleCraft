package com.github.manolo8.simplecraft.modules.mob;

import com.github.manolo8.simplecraft.SimpleCraft;
import com.github.manolo8.simplecraft.modules.user.User;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attributable;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;

import java.util.List;
import java.util.Random;

public abstract class Mob {

    protected final String name;
    protected final Random random;

    public Mob(String name, Random random) {
        this.name = name;
        this.random = random;
    }

    public boolean match(Entity entity) {
        List<MetadataValue> metadata = entity.getMetadata("mob");

        if (metadata == null || metadata.size() == 0) return false;

        return metadata.get(0).asString().equals(name);
    }

    public Entity spawnMobWithDetails(Location location) {
        Entity entity = spawnMob(location);
        entity.setCustomNameVisible(true);
        entity.setMetadata("mob", new FixedMetadataValue(SimpleCraft.instance, name));
        return entity;
    }

    protected abstract Entity spawnMob(Location location);

    protected abstract List<ItemStack> getDrops();

    protected void attack(Entity damager, Player target) {
    }

    protected void attacked(Entity target, Player damager) {
        Damageable damageable = (Damageable) target;

        Bukkit.getScheduler().runTaskLater(SimpleCraft.instance, () -> {
            int life = (int) damageable.getHealth();
            int max = (int) ((Attributable) target).getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
            target.setCustomName("§b♥ " + life + "/" + max);
        }, 1);
    }

    protected abstract void playerKill(User killer);

    protected void splashPotion(Location location, PotionType type, boolean extended, boolean upgraded) {
        ItemStack stack = new ItemStack(Material.SPLASH_POTION);

        PotionMeta meta = (PotionMeta) stack.getItemMeta();
        meta.setBasePotionData(new PotionData(type, extended, upgraded));

        stack.setItemMeta(meta);


        ThrownPotion thrownPotion = (ThrownPotion) location.getWorld().spawnEntity(location, EntityType.SPLASH_POTION);
        thrownPotion.setItem(stack);
    }
}
