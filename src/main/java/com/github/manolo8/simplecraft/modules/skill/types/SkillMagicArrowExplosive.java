package com.github.manolo8.simplecraft.modules.skill.types;

import com.github.manolo8.simplecraft.modules.skill.MagicLevel;
import com.github.manolo8.simplecraft.modules.skill.Skill;
import com.github.manolo8.simplecraft.modules.skill.SkillMagic;
import com.github.manolo8.simplecraft.modules.skill.tools.ProjectileHit;
import com.github.manolo8.simplecraft.modules.user.User;
import com.github.manolo8.simplecraft.utils.ItemStackUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.*;
import org.bukkit.util.Vector;

import java.util.List;

public class SkillMagicArrowExplosive extends SkillMagic {

    private static MagicLevel[] levels;

    static {
        levels = new MagicLevel[10];

        levels[0] = new Beginner(1, 2, 6, 15);
        levels[1] = new Beginner(3, 2, 5, 18);
        levels[2] = new Beginner(9, 3, 5, 20);
        levels[3] = new Beginner(15, 3, 5, 22);
        levels[4] = new Beginner(21, 3, 5, 25);
        levels[5] = new Beginner(29, 3, 5, 30);
        levels[6] = new Beginner(35, 4, 4, 35);
        levels[7] = new Beginner(42, 4, 4, 38);
        levels[8] = new Beginner(49, 5, 4, 40);
        levels[9] = new Beginner(55, 5, 3, 50);
    }

    public SkillMagicArrowExplosive() {
        super(levels, "Flexa explosiva", 5, Material.ARROW, ItemStackUtils.create(Material.STICK, "§aFlexa explosiva"));
    }

    @Override
    public Skill newInstance() {
        return new SkillMagicArrowExplosive();
    }

    static class Beginner extends MagicLevel implements ProjectileHit {

        private int damage;

        public Beginner(int upgradeAmount, int max, int cooldown, int damage) {
            super(upgradeAmount, max, cooldown);
            this.info = new String[]{"§aVarinha da flexa explosiva.",
                    "§a(Armazena até " + max + " flexas)",
                    "§aDano de " + damage,
                    "§aCooldown de " + cooldown + "s.",
                    "§cDá metade do dano em jogadores"};
            this.damage = damage;
        }

        @Override
        public void handle(User user) {
            Player base = user.getBase();

            Vector direction = base.getLocation().getDirection();

            Fireball fireball= base.launchProjectile(Fireball.class, direction.multiply(4));
            fireball.setIsIncendiary(false);
        }

        @Override
        public void onProjectileHit(User user, Projectile projectile) {
            if (!(projectile instanceof Fireball)) return;
            Fireball fireball = (Fireball) projectile;

            if (fireball.isIncendiary()) return;

            List<Entity> entities = fireball.getNearbyEntities(5, 5, 5);

            for (Entity entity : entities) {
                if (!(entity instanceof LivingEntity) || entity == user.getBase()) continue;

                LivingEntity living = (LivingEntity) entity;

                living.damage(entity instanceof Player ? (damage / 2) : damage, user.getBase());
            }

            Location loc = fireball.getLocation();

            fireball.getWorld().spawnParticle(Particle.EXPLOSION_LARGE, loc, 5);
            fireball.getWorld().playSound(loc, Sound.ENTITY_GENERIC_EXPLODE, 20, 20);

            fireball.remove();
        }
    }
}