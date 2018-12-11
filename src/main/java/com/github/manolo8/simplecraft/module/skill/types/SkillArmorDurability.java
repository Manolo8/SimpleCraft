package com.github.manolo8.simplecraft.module.skill.types;

import com.github.manolo8.simplecraft.module.skill.Level;
import com.github.manolo8.simplecraft.module.skill.Skill;
import com.github.manolo8.simplecraft.module.skill.tools.DamageItemResult;
import com.github.manolo8.simplecraft.module.skill.tools.ItemDamage;
import org.bukkit.Material;

import java.util.Arrays;

public class SkillArmorDurability extends Skill<Level> {

    private static Level[] levels;

    static {
        levels = new Level[16];

        levels[0] = new Beginner(1, 0.5, 0.04);
        levels[1] = new Beginner(10, 0.65, 0.12);
        levels[2] = new Beginner(20, 0.85, 0.20);
        levels[3] = new Beginner(30, 1, 0.28);
        levels[4] = new Beginner(40, 1.15, 0.34);
        levels[5] = new Beginner(50, 1.25, 0.42);
        levels[6] = new Beginner(60, 1.3, 0.50);
        levels[7] = new Beginner(70, 1.37, 0.54);
        levels[8] = new Beginner(80, 1.75, 0.58);
        levels[9] = new Beginner(90, 2.12, 0.62);
        levels[10] = new Beginner(100, 2.4, 0.66);
        levels[11] = new Beginner(110, 2.8, 0.70);
        levels[12] = new Beginner(120, 3.2, 0.74);
        levels[13] = new Beginner(130, 3.6, 0.78);
        levels[14] = new Beginner(140, 4, 0.80);
        levels[15] = new Beginner(150, 4.5, 0.85);
    }

    public SkillArmorDurability() {
        super(levels, "Armor++", 2, Material.DIAMOND_CHESTPLATE);
    }

    @Override
    public Skill newInstance() {
        return new SkillArmorDurability();
    }

    static class Beginner extends Level implements ItemDamage {

        private double chance;

        public Beginner(int upgradeAmount, double cost, double chance) {
            super(upgradeAmount, cost);
            this.chance = chance;
            this.info = Arrays.asList("§aCancela o dano a armadura.",
                    "§a(Tem " + df.format(chance * 100) + "% de cancelar o dano)",
                    "§c(Custa " + cost + " mana por (item * ataque))");
        }

        @Override
        public void onReceive(DamageItemResult result) {
            if (skill.getUser().iContainer().protection().canUseSkill(this.skill.getType()) && skill.takeMana()) {
                result.setChance(chance);
            }
        }
    }
}
