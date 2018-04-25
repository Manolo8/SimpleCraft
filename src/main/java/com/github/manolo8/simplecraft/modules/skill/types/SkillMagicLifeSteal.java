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

import java.util.List;

public class SkillMagicLifeSteal extends SkillMagic {

    private static MagicLevel[] levels;

    static {
        levels = new MagicLevel[10];

        levels[0] = new Beginner(1, 1, 39, 1, 1);
        levels[1] = new Beginner(3, 1, 38, 1, 1);
        levels[2] = new Beginner(5, 1, 37, 1, 1);
        levels[3] = new Beginner(7, 1, 36, 1, 1);
        levels[4] = new Beginner(9, 1, 35, 2, 1);
        levels[5] = new Beginner(11, 1, 34, 2, 2);
        levels[6] = new Beginner(13, 1, 33, 2, 2);
        levels[7] = new Beginner(15, 1, 32, 2, 2);
        levels[8] = new Beginner(18, 1, 31, 2, 3);
        levels[9] = new Beginner(23, 1, 21, 2, 4);
    }

    public SkillMagicLifeSteal() {
        super(levels, "Roubo de vida", 6, Material.GOLDEN_APPLE, ItemStackUtils.create(Material.STICK, "§aRoubo de vida"));
    }

    @Override
    public Skill newInstance() {
        return new SkillMagicLifeSteal();
    }

    static class Beginner extends MagicLevel implements Tickable {

        private int counter;
        private int steal;
        private int quantity;

        public Beginner(int upgradeAmount, int max, int cooldown, int steal, int quantity) {
            super(upgradeAmount, max, cooldown);
            this.info = new String[]{"§aVarinha do roubo de vida.",
                    "§a(Quando ativa, rouba até",
                    "§a" + steal + " de vida por segundo",
                    "§ade até " + quantity + " jogador(res)",
                    "§aDurante 10s)"};
            this.steal = steal;
            this.quantity = quantity;
        }

        @Override
        public void handle(User user) {
            counter = 20 * 10;
        }

        @Override
        public void tick(User user) {
            if (counter <= 0) return;
            counter -= 2;
            if(counter % 20 != 0) return;

            if(user.getBase().isDead()) return;

            user.playSound(Sound.BLOCK_LAVA_EXTINGUISH, 20, 20);
            Player base = user.getBase();

            List<Entity> entities = base.getNearbyEntities(5, 5, 5);

            int current = 0;
            double total = 0;

            for (Entity entity : entities) {
                if (current == quantity) break;
                if (!(entity instanceof LivingEntity)) continue;
                current++;
                LivingEntity living = (LivingEntity) entity;

                living.damage(steal, base);

                total += living.getLastDamage();

                createParticles(base.getLocation(), living.getLocation());
            }

            double newHealth = base.getHealth() + total;
            base.setHealth(newHealth > base.getMaxHealth() ? base.getMaxHealth() : newHealth);
        }

        private void createParticles(Location origin, Location destination) {
            Vector target = destination.toVector();
            origin.setDirection(target.subtract(origin.toVector()));
            Vector increase = origin.getDirection().multiply(1.6);
            for (int counter = 0; counter < 3; counter++) {
                Location loc = origin.add(increase);
                origin.getWorld().spawnParticle(Particle.HEART, loc, 1);
            }
        }
    }
}
