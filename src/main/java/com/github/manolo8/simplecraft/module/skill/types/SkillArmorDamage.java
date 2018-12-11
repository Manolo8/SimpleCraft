package com.github.manolo8.simplecraft.module.skill.types;

import com.github.manolo8.simplecraft.module.skill.Level;
import com.github.manolo8.simplecraft.module.skill.Skill;
import com.github.manolo8.simplecraft.module.skill.tools.DamageItemResult;
import com.github.manolo8.simplecraft.module.skill.tools.ItemDamage;
import org.bukkit.Material;

import java.util.Arrays;

public class SkillArmorDamage extends Skill<Level> {

    private static Level[] levels;

    static {
        levels = new Level[16];

        levels[0] = new Beginner(1, 10, 1);
        levels[1] = new Beginner(10, 11, 3);
        levels[2] = new Beginner(20, 11, 6);
        levels[3] = new Beginner(30, 12, 8);
        levels[4] = new Beginner(40, 13, 10);
        levels[5] = new Beginner(50, 13, 12);
        levels[6] = new Beginner(60, 14, 14);
        levels[7] = new Beginner(70, 14, 16);
        levels[8] = new Beginner(80, 15, 18);
        levels[9] = new Beginner(90, 15, 20);
        levels[10] = new Beginner(100, 16, 22);
        levels[11] = new Beginner(110, 17, 24);
        levels[12] = new Beginner(120, 18, 26);
        levels[13] = new Beginner(130, 19, 28);
        levels[14] = new Beginner(140, 20, 30);
        levels[15] = new Beginner(150, 21, 32);
    }

    public SkillArmorDamage() {
        super(levels, "Anti armor", 1, Material.DIAMOND_AXE);
    }

    @Override
    public Skill newInstance() {
        return new SkillArmorDamage();
    }

    static class Beginner extends Level implements ItemDamage {

        private int damage;

        public Beginner(int upgradeAmount, double cost, int damage) {
            super(upgradeAmount, cost);
            this.damage = damage;
            this.info = Arrays.asList("§aDá dano extra na armadura.",
                    "§a(Dá " + damage + " de dano na armadura do inimigo)",
                    "§c(Custa " + cost + " de mana por (item * ataque))");
        }

        @Override
        public void onGive(DamageItemResult result) {

            if (skill.getUser().iContainer().protection().canUseSkill(this.skill.getType()) && skill.takeMana()) {
                result.increase(damage);
            }

        }
    }
}
