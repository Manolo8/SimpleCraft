package com.github.manolo8.simplecraft.modules.skill.types;

import com.github.manolo8.simplecraft.modules.skill.Level;
import com.github.manolo8.simplecraft.modules.skill.Skill;
import com.github.manolo8.simplecraft.modules.skill.tools.DamageResult;
import com.github.manolo8.simplecraft.modules.skill.tools.ReceiveDamage;
import com.github.manolo8.simplecraft.modules.user.User;
import com.github.manolo8.simplecraft.utils.PotionUtils;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffectType;

public class SkillDodgeDamage extends Skill<Level> {

    private static Level[] levels;

    static {
        levels = new Level[10];

        levels[0] = new Beginner(1, 12);
        levels[1] = new Beginner(3, 11);
        levels[2] = new Beginner(6, 10);
        levels[3] = new Beginner(10, 9);
        levels[4] = new Beginner(15, 8);
        levels[5] = new Intermediate(25, 7);
        levels[6] = new Intermediate(27, 6);
        levels[7] = new Intermediate(30, 5);
        levels[8] = new Intermediate(35, 4);
        levels[9] = new Intermediate(40, 3);
    }

    public SkillDodgeDamage() {
        super(levels, "Desvio de dano", 0, Material.WEB);
    }

    @Override
    public Skill newInstance() {
        return new SkillDodgeDamage();
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
            if (random.nextInt(chance) != 1) return;

            damageResult.setCancelled(true);

            user.playSound(Sound.ITEM_SHIELD_BLOCK, 20, 20);
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
