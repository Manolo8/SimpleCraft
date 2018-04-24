package com.github.manolo8.simplecraft.modules.skill.types;

import com.github.manolo8.simplecraft.modules.skill.Level;
import com.github.manolo8.simplecraft.modules.skill.Skill;
import com.github.manolo8.simplecraft.modules.skill.tools.DamageResult;
import com.github.manolo8.simplecraft.modules.skill.tools.GiveDamage;
import com.github.manolo8.simplecraft.modules.user.User;
import com.github.manolo8.simplecraft.utils.PotionUtils;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffectType;

import java.util.Random;

public class SkillExtraDamage extends Skill {

    private static Level[] levels;
    private static Random random;

    static {
        levels = new Level[10];

        levels[0] = new Beginner(1, 1.1);
        levels[1] = new Beginner(3, 1.2);
        levels[2] = new Beginner(6, 1.25);
        levels[3] = new Beginner(10, 1.32);
        levels[4] = new Beginner(15, 1.35);
        levels[5] = new Intermediate(25, 1.5, 40);
        levels[6] = new Intermediate(27, 1.55, 30);
        levels[7] = new Intermediate(30, 1.59, 20);
        levels[8] = new Intermediate(35, 1.65, 10);
        levels[9] = new Intermediate(40, 1.75, 8);
    }

    public SkillExtraDamage(Random rnd) {
        random = rnd;
    }

    @Override
    public int getType() {
        return 1;
    }

    @Override
    public Level getLevelHandler() {
        return levels[level];
    }

    @Override
    public boolean hasNextLevel() {
        return levels.length > level;
    }

    static class Beginner extends Level implements GiveDamage {

        private double multiply;

        public Beginner(int upgradeAmount, double multiply) {
            super(upgradeAmount);
            this.multiply = multiply;
            this.info = new String[]{"§aDá dano extra em jogadores.",
                    "§a(Multiplica o dano por " + multiply + ")"};
        }

        @Override
        public void onGiveDamage(User user, LivingEntity entity, DamageResult damageResult) {
            damageResult.multiply(multiply);
        }
    }

    static class Intermediate extends Beginner {

        private int chance;

        public Intermediate(int upgradeAmount, double multiply, int chance) {
            super(upgradeAmount, multiply);
            this.chance = chance;
            this.info = new String[]{"§aDá dano extra em jogadores.",
                    "§a(Multiplica o dano por " + multiply + ")"
                    , "§aTem 1 chance em " + chance + " de aplicar lentidão."};

        }

        @Override
        public void onGiveDamage(User user, LivingEntity entity, DamageResult damageResult) {
            super.onGiveDamage(user, entity, damageResult);

            if (random.nextInt(chance) == 1)
                PotionUtils.applyPotion(entity, PotionEffectType.SLOW, 5, 2);
        }
    }
}
