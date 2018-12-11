package com.github.manolo8.simplecraft.module.skill.types;

import com.github.manolo8.simplecraft.module.skill.MagicLevel;
import com.github.manolo8.simplecraft.module.skill.Skill;
import com.github.manolo8.simplecraft.module.skill.SkillMagic;
import com.github.manolo8.simplecraft.module.skill.tools.Damage;
import com.github.manolo8.simplecraft.utils.mc.ItemStackUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.*;
import org.bukkit.util.Vector;

import java.util.Arrays;
import java.util.List;

public class SkillMagicArrowExplosive extends SkillMagic {

    private static MagicLevel[] levels;

    static {
        levels = new MagicLevel[8];

        levels[0] = new Beginner(1, 25, 4, 4, 5);
        levels[1] = new Beginner(20, 25, 4, 4, 7);
        levels[2] = new Beginner(40, 30, 5, 4, 9);
        levels[3] = new Beginner(60, 30, 5, 4, 11);
        levels[4] = new Beginner(80, 35, 5, 4, 16);
        levels[5] = new Beginner(100, 35, 5, 4, 22);
        levels[6] = new Beginner(120, 40, 6, 3, 26);
        levels[7] = new Beginner(150, 40, 6, 3, 30);
    }

    public SkillMagicArrowExplosive() {
        super(levels, "Flexa explosiva", 7, Material.ARROW, ItemStackUtils.create(Material.STICK, "§aFlexa explosiva"));
    }

    @Override
    public Skill newInstance() {
        return new SkillMagicArrowExplosive();
    }

    static class Beginner extends MagicLevel implements Damage {

        private int damage;

        public Beginner(int upgradeAmount, int cost, int max, int cooldown, int damage) {
            super(upgradeAmount, cost, max, cooldown);
            this.info = Arrays.asList("§aVarinha da flexa explosiva.",
                    "§a(Armazena até " + max + " flexas)",
                    "§aDano de " + damage,
                    "§aCooldown de " + cooldown + "s.",
                    "§c(Custa " + cost + " de mana por uso)");
            this.damage = damage;
        }

        @Override
        public void handle() {
            Player base = user().base();

            Vector direction = base.getLocation().getDirection();

            Fireball fireball = base.launchProjectile(Fireball.class, direction.multiply(4));
            fireball.setIsIncendiary(false);
        }

        @Override
        public void onProjectileHit(Projectile projectile) {
            if (!(projectile instanceof Fireball)) return;
            Fireball fireball = (Fireball) projectile;

            if (fireball.isIncendiary()) return;

            List<Entity> entities = fireball.getNearbyEntities(5, 5, 5);

            for (Entity entity : entities) {
                if (!(entity instanceof LivingEntity) || entity == user().base()) continue;

                LivingEntity living = (LivingEntity) entity;

                living.damage(living instanceof Player ? damage / 2 : damage, user().base());
            }

            Location loc = fireball.getLocation();

            fireball.getWorld().spawnParticle(Particle.EXPLOSION_LARGE, loc, 5);
            fireball.getWorld().playSound(loc, Sound.ENTITY_GENERIC_EXPLODE, 20, 20);

            fireball.remove();
        }
    }
}