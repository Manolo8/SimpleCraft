package com.github.manolo8.simplecraft.modules.skill.types;

import com.github.manolo8.simplecraft.modules.skill.MagicLevel;
import com.github.manolo8.simplecraft.modules.skill.Skill;
import com.github.manolo8.simplecraft.modules.skill.SkillMagic;
import com.github.manolo8.simplecraft.modules.skill.tools.Tickable;
import com.github.manolo8.simplecraft.modules.user.User;
import com.github.manolo8.simplecraft.utils.ItemStackUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.Collection;

public class SkillMagicAreaDamage extends SkillMagic {

    private static MagicLevel[] levels;

    static {
        levels = new MagicLevel[10];

        levels[0] = new Beginner(1, 1, 18, 20);
        levels[1] = new Beginner(3, 1, 17, 22);
        levels[2] = new Beginner(9, 1, 17, 23);
        levels[3] = new Beginner(15, 1, 16, 25);
        levels[4] = new Beginner(21, 2, 16, 27);
        levels[5] = new Beginner(29, 2, 15, 29);
        levels[6] = new Beginner(35, 2, 15, 33);
        levels[7] = new Beginner(42, 2, 14, 35);
        levels[8] = new Beginner(49, 2, 14, 38);
        levels[9] = new Beginner(55, 2, 12, 45);
    }

    public SkillMagicAreaDamage() {
        super(levels, "Dano em área mágico", 3, Material.TNT, ItemStackUtils.create(Material.STICK, "§aDano em área mágico"));
    }

    @Override
    public Skill newInstance() {
        return new SkillMagicAreaDamage();
    }

    static class Beginner extends MagicLevel implements Tickable {

        private int damage;
        private int counter;
        private Location origin;
        private Vector increase;


        public Beginner(int upgradeAmount, int max, int cooldown, int damage) {
            super(upgradeAmount, max, cooldown);
            this.damage = damage;
            this.info = new String[]{"§aVarinha que da dano em área.",
                    "§a(Da " + damage + " de dano em área)",
                    "§aArmazena até " + max + " cargas",
                    "§aCooldown de " + cooldown + "s.",
                    "§cDá metade do dano em jogadores"};
        }

        @Override
        public void handle(User user) {
            counter = 64;

            origin = user.getBase().getLocation();
            increase = origin.getDirection().multiply(4);
            origin.add(increase);
        }

        @Override
        public void tick(User user) {
            if (counter <= 0) return;
            counter -= 8;

            Location loc = origin;

            if (counter % 16 == 0) {
                origin.getWorld().spawnParticle(Particle.EXPLOSION_HUGE, loc, 2);
                origin.getWorld().playSound(loc, Sound.ENTITY_GENERIC_EXPLODE, 10, 10);

                giveDamage(user.getBase(), loc);

                origin.add(increase);
            } else {
                origin.getWorld().spawnParticle(Particle.EXPLOSION_LARGE, loc, 5);
                origin.getWorld().playSound(loc, Sound.ENTITY_TNT_PRIMED, 10, 10);
            }

            if (counter <= 0) {
                origin = null;
                increase = null;
            }
        }

        private void giveDamage(Player base, Location loc) {
            Collection<Entity> entities = loc.getWorld().getNearbyEntities(loc, 5, 5, 5);

            for (Entity entity : entities) {
                if (!(entity instanceof LivingEntity) || base == entity) continue;

                LivingEntity living = (LivingEntity) entity;

                living.damage(entity instanceof Player ? (damage / 2) : damage, base);
            }
        }
    }

}
