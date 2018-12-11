package com.github.manolo8.simplecraft.module.skill.types;

import com.github.manolo8.simplecraft.module.skill.Level;
import com.github.manolo8.simplecraft.module.skill.Skill;
import com.github.manolo8.simplecraft.module.skill.tools.Damage;
import com.github.manolo8.simplecraft.module.skill.tools.DamageResult;
import com.github.manolo8.simplecraft.module.skill.tools.DisableSkill;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.LivingEntity;

import java.util.Arrays;

public class SkillResistance extends Skill<Level> implements DisableSkill {

    private static Level[] levels;

    static {
        levels = new Level[16];

        levels[0] = new Beginner(1, 5, 0.10);
        levels[1] = new Beginner(10, 6, 0.12);
        levels[2] = new Beginner(20, 6, 0.14);
        levels[3] = new Beginner(30, 7, 0.16);
        levels[4] = new Beginner(40, 7, 0.18);
        levels[5] = new Beginner(50, 7, 0.22);
        levels[6] = new Beginner(60, 8, 0.26);
        levels[7] = new Beginner(70, 8, 0.30);
        levels[8] = new Beginner(80, 9, 0.34);
        levels[9] = new Beginner(90, 9, 0.38);
        levels[10] = new Beginner(100, 10, 0.42);
        levels[11] = new Beginner(110, 10, 0.46);
        levels[12] = new Beginner(120, 11, 0.50);
        levels[13] = new Beginner(130, 12, 0.54);
        levels[14] = new Beginner(140, 13, 0.62);
        levels[15] = new Beginner(150, 14, 0.7);
    }

    public SkillResistance() {
        super(levels, "Resistência", 3, Material.GLISTERING_MELON_SLICE);
    }

    @Override
    public Skill newInstance() {
        return new SkillResistance();
    }

    @Override
    public void onDisable() {
        getUser().base().getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20);
    }

    static class Beginner extends Level implements Damage {

        private double resistance;

        public Beginner(int upgradeAmount, int cost, double resistance) {
            super(upgradeAmount, cost);
            this.info = Arrays.asList("§aDá resistência",
                    "§a(Resiste a " + (resistance * 100) + "% do dano)",
                    "§c(Custa " + cost + " de mana por uso)");
            this.resistance = 1 - resistance;
        }

        @Override
        public void onReceive(LivingEntity entity, DamageResult damageResult) {
            if (skill.getUser().iContainer().protection().canUseSkill(this.skill.getType()) && skill.takeMana()) {
                damageResult.multiply(resistance);
            }
        }
    }
}
