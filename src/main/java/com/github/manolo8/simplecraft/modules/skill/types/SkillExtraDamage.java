package com.github.manolo8.simplecraft.modules.skill.types;

import com.github.manolo8.simplecraft.modules.skill.Level;
import com.github.manolo8.simplecraft.modules.skill.Skill;
import com.github.manolo8.simplecraft.modules.skill.tools.DamageResult;
import com.github.manolo8.simplecraft.modules.skill.tools.GiveDamage;
import com.github.manolo8.simplecraft.modules.user.User;
import com.github.manolo8.simplecraft.utils.PotionUtils;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

public class SkillExtraDamage extends Skill<Level> {

    private static Level[] levels;

    static {
        levels = new Level[16];

        levels[0] = new Beginner(1, 0.10);
        levels[1] = new Beginner(3, 0.16);
        levels[2] = new Beginner(6, 0.22);
        levels[3] = new Beginner(10, 0.36);
        levels[4] = new Beginner(15, 0.44);
        levels[5] = new Intermediate(25, 0.52, 10);
        levels[6] = new Intermediate(27, 0.58, 8);
        levels[7] = new Intermediate(30, 0.66, 7);
        levels[8] = new Intermediate(35, 0.78, 5);
        levels[9] = new Intermediate(40, 0.90, 3);
        levels[10] = new Intermediate(45, 1.0, 3);
        levels[11] = new Intermediate(50, 1.12, 3);
        levels[12] = new Intermediate(55, 1.26, 3);
        levels[13] = new Intermediate(60, 1.46, 3);
        levels[14] = new Intermediate(65, 1.70, 3);
        levels[15] = new Intermediate(70, 2.0, 3);
    }

    public SkillExtraDamage() {
        super(levels, "Dano extra", 1, Material.BLAZE_ROD);
    }

    @Override
    public Skill newInstance() {
        return new SkillExtraDamage();
    }

    static class Beginner extends Level implements GiveDamage {

        private double multiply;

        public Beginner(int upgradeAmount, double multiply) {
            super(upgradeAmount);
            this.multiply = multiply;
            this.info = new String[]{"§aDá dano extra em jogadores.",
                    "§a(Aumenta o dano em " + (multiply + 1.0) * 100 + "%",
                    "§aE o dano mágico em " + (multiply + 1.0) * 50 + "%)"};
        }

        @Override
        public void onGiveDamage(User user, LivingEntity entity, DamageResult damageResult) {
            ItemStack stack = user.getBase().getItemInHand();

            if (stack != null) {
                Material material = stack.getType();
                if (material == Material.DIAMOND_SWORD
                        || material == Material.GOLD_SWORD
                        || material == Material.STONE_SWORD
                        || material == Material.IRON_SWORD
                        || material == Material.WOOD_SWORD) {
                    damageResult.multiply(multiply + 1);
                    user.sendAction(damageResult.getDamage());
                    return;
                }
            }

            damageResult.multiply((multiply * 0.5) + 1);
        }
    }

    static class Intermediate extends Beginner {

        private int chance;

        public Intermediate(int upgradeAmount, double multiply, int chance) {
            super(upgradeAmount, multiply);
            this.chance = chance;
            this.info = new String[]{"§aDá dano extra em jogadores.",
                    "§a(Aumenta o dano em " + (multiply + 1.0) * 100 + "%",
                    "§aE o dano mágico em " + (multiply + 1.0) * 50 + "%)",
                    "§aTem 1 chance em " + chance + " de aplicar lentidão."};

        }

        @Override
        public void onGiveDamage(User user, LivingEntity entity, DamageResult damageResult) {
            super.onGiveDamage(user, entity, damageResult);

            if (random.nextInt(chance) == 1)
                PotionUtils.applyPotion(entity, PotionEffectType.SLOW, 5, 4);
        }
    }
}
