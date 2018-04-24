package com.github.manolo8.simplecraft.modules.skill.types;

import com.github.manolo8.simplecraft.modules.skill.Level;
import com.github.manolo8.simplecraft.modules.skill.Skill;
import com.github.manolo8.simplecraft.modules.skill.tools.DamageResult;
import com.github.manolo8.simplecraft.modules.skill.tools.ReceiveDamage;
import com.github.manolo8.simplecraft.modules.user.User;
import com.github.manolo8.simplecraft.utils.PotionUtils;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffectType;

import java.util.Random;

public class SkillDodgeDamage extends Skill {

    private static Level[] levels;
    private static Random random;

    static {
        levels = new Level[10];

        levels[0] = new Beginner(1, 100);
        levels[1] = new Beginner(3, 97);
        levels[2] = new Beginner(6, 95);
        levels[3] = new Beginner(10, 93);
        levels[4] = new Beginner(15, 90);
        levels[5] = new Intermediate(25, 88);
        levels[6] = new Intermediate(27, 85);
        levels[7] = new Intermediate(30, 83);
        levels[8] = new Intermediate(35, 80);
        levels[9] = new Intermediate(40, 75);
    }

    public SkillDodgeDamage(Random rnd) {
        random = rnd;
    }

    @Override
    public int getType() {
        return 0;
    }

    @Override
    public Level getLevelHandler() {
        return levels[level];
    }

    @Override
    public boolean hasNextLevel() {
        return levels.length > level;
    }

    static class Beginner extends Level implements ReceiveDamage {

        private int chance;

        public Beginner(int upgradeAmount, int chance) {
            super(upgradeAmount);
            this.chance = chance;
            this.info = new String[]{"§aDesvia do dano inimigo.",
                    "§a(Chance 1 em " + chance + " de desviar)"};
        }

        @Override
        public void onReceiveDamage(LivingEntity entity, User user, DamageResult damageResult) {
            if (random.nextInt(chance) == 1) damageResult.setDamage(0);
        }
    }

    static class Intermediate extends Beginner {

        public Intermediate(int upgradeAmount, int chance) {
            super(upgradeAmount, chance);
            this.info = new String[]{"§aDesvia do dano inimigo.",
                    "§a(Chance 1 em " + chance + " de desviar)",
                    "§a(Algumas vezes dá poção de velocidade)"};

        }

        @Override
        public void onReceiveDamage(LivingEntity entity, User user, DamageResult damageResult) {
            super.onReceiveDamage(entity, user, damageResult);

            if (random.nextInt(15) == 1)
                PotionUtils.applyPotion(entity, PotionEffectType.SPEED, 10, 3);
        }
    }
}
