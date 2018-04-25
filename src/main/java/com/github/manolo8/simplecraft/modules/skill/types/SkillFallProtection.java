package com.github.manolo8.simplecraft.modules.skill.types;

import com.github.manolo8.simplecraft.modules.skill.Level;
import com.github.manolo8.simplecraft.modules.skill.Skill;
import com.github.manolo8.simplecraft.modules.skill.tools.DamageResult;
import com.github.manolo8.simplecraft.modules.skill.tools.FallDamage;
import com.github.manolo8.simplecraft.modules.skill.tools.JoinSkill;
import com.github.manolo8.simplecraft.modules.user.User;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;

public class SkillFallProtection extends Skill<Level> {

    private static Level[] levels;

    static {
        levels = new Level[10];

        levels[0] = new Beginner(1, 0.8);
        levels[1] = new Beginner(3, 0.75);
        levels[2] = new Beginner(9, 0.7);
        levels[3] = new Beginner(15, 0.6);
        levels[4] = new Beginner(21, 0.5);
        levels[5] = new Beginner(29, 0.4);
        levels[6] = new Beginner(35, 0.3);
        levels[7] = new Beginner(42, 0.2);
        levels[8] = new Beginner(49, 0.15);
        levels[9] = new Beginner(55, 0.1);
    }

    public SkillFallProtection() {
        super(levels, "Redução de queda", 7, Material.SLIME_BLOCK);
    }

    @Override
    public Skill newInstance() {
        return new SkillFallProtection();
    }

    static class Beginner extends Level implements FallDamage {

        private double reduction;

        public Beginner(int upgradeAmount, double reduction) {
            super(upgradeAmount);
            this.reduction = reduction;
            this.info = new String[]{"§aReduz o dano de queda.",
                    "§a(Você sofre apenas " + (reduction * 100) + "% do dano total)"};
        }

        @Override
        public void onFallDamage(User user, DamageResult result) {
            result.multiply(reduction);

            user.playSound(Sound.ENTITY_SLIME_JUMP, 10, 10);
        }
    }
}
