package com.github.manolo8.simplecraft.module.skill.types;

import com.github.manolo8.simplecraft.module.skill.Level;
import com.github.manolo8.simplecraft.module.skill.Skill;
import com.github.manolo8.simplecraft.module.skill.tools.Damage;
import com.github.manolo8.simplecraft.module.skill.tools.DamageResult;
import org.bukkit.Material;
import org.bukkit.Sound;

import java.util.Arrays;

public class SkillFallProtection extends Skill<Level> {

    private static Level[] levels;

    static {
        levels = new Level[8];

        levels[0] = new Beginner(1, 15, 0.8);
        levels[1] = new Beginner(10, 20, 0.75);
        levels[2] = new Beginner(20, 20, 0.7);
        levels[3] = new Beginner(30, 25, 0.6);
        levels[4] = new Beginner(40, 25, 0.5);
        levels[5] = new Beginner(50, 30, 0.4);
        levels[6] = new Beginner(60, 30, 0.3);
        levels[7] = new Beginner(70, 35, 0.2);
    }

    public SkillFallProtection() {
        super(levels, "Redução de queda", 5, Material.SLIME_BLOCK);
    }

    @Override
    public Skill newInstance() {
        return new SkillFallProtection();
    }

    static class Beginner extends Level implements Damage {

        private double reduction;

        public Beginner(int upgradeAmount, int cost, double reduction) {
            super(upgradeAmount, cost);
            this.reduction = reduction;
            this.info = Arrays.asList("§aReduz o dano de queda.",
                    "§a(Você sofre apenas " + df.format(reduction * 100) + "% do dano total)",
                    "§c(Custa " + cost + " de mana por uso)");
        }

        @Override
        public void onFall(DamageResult result) {
            if (skill.getUser().iContainer().protection().canUseSkill(this.skill.getType()) && skill.takeMana()) {

                result.multiply(reduction);

                user().playSound(Sound.ENTITY_SLIME_JUMP, 10, 10, true);
            }
        }
    }
}
