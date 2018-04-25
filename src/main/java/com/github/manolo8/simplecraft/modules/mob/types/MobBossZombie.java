package com.github.manolo8.simplecraft.modules.mob.types;

import com.github.manolo8.simplecraft.modules.mob.Mob;
import com.github.manolo8.simplecraft.modules.user.User;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attributable;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionType;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MobBossZombie extends Mob {

    private final MobZombie zombie;
    private final Entity[] zombies;

    public MobBossZombie(Random random, MobZombie zombie) {
        super("§4Boss Zombie", random);
        this.zombie = zombie;
        this.zombies = new Entity[3];
    }

    @Override
    protected Entity spawnMob(Location location) {
        Zombie zombie = location.getWorld().spawn(location, Zombie.class);

        zombie.setBaby(false);
        zombie.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(120);
        zombie.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0.4);
        zombie.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(5);
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

        splashPotion(target.getLocation(), PotionType.INSTANT_DAMAGE, false, true);
    }

    @Override
    protected void attacked(Entity target, Player damager) {
        super.attacked(target,damager);
        if (random.nextInt(3) == 1) {

            for (int i = 0; i < zombies.length; i++) {
                Entity entity = zombies[i];
                if (entity == null || entity.isDead()) zombies[i] = zombie.spawnMobWithDetails(target.getLocation());
                if (random.nextBoolean()) break;
            }
        }

        if (random.nextInt(8) == 1) {
            List<Entity> entities = target.getNearbyEntities(5, 5, 5);

            for (Entity entity : entities) {
                if (zombie.match(entity)) {
                    ((Attributable) entity).getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0.5);
                    entity.getWorld().playEffect(entity.getLocation(), Effect.MOBSPAWNER_FLAMES, 0, 20);
                }
            }
        }
    }

    @Override
    protected void playerKill(User killer) {
        killer.giveExp(50);
    }
}
