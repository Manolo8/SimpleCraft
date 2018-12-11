package com.github.manolo8.simplecraft.module.skill.types;

import com.github.manolo8.simplecraft.module.skill.Level;
import com.github.manolo8.simplecraft.module.skill.Skill;
import com.github.manolo8.simplecraft.module.skill.tools.Damage;
import com.github.manolo8.simplecraft.module.skill.tools.DamageResult;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

public class SkillExtraDamage extends Skill<Level> {

    private static Level[] levels;

    static {
        levels = new Level[16];

        levels[0] = new Beginner(1, 5, 0.5);
        levels[1] = new Beginner(10, 5, 0.8);
        levels[2] = new Beginner(20, 6, 1.2);
        levels[3] = new Beginner(30, 6, 1.6);
        levels[4] = new Beginner(40, 7, 2);
        levels[5] = new Beginner(50, 7, 2.6);
        levels[6] = new Beginner(60, 8, 3.4);
        levels[7] = new Beginner(70, 8, 4);
        levels[8] = new Beginner(80, 9, 5);
        levels[9] = new Beginner(90, 9, 6);
        levels[10] = new Beginner(100, 10, 7);
        levels[11] = new Beginner(110, 10, 8);
        levels[12] = new Beginner(120, 12, 9);
        levels[13] = new Beginner(130, 14, 10);
        levels[14] = new Beginner(140, 16, 11);
        levels[15] = new Beginner(150, 18, 12);
    }

    public SkillExtraDamage() {
        super(levels, "Dano++", 0, Material.DIAMOND_SWORD);
    }

    @Override
    public Skill newInstance() {
        return new SkillExtraDamage();
    }

    static class Beginner extends Level implements Damage {

        private double extra;

        public Beginner(int upgradeAmount, int cost, double extra) {
            super(upgradeAmount, cost);
            this.extra = extra;
            this.info = Arrays.asList("§aDá dano extra em tudo.",
                    "§a(Aumenta o dano em " + extra,
                    "§aE o dano mágico em " + (extra / 2d) + ")",
                    "§c(Custa " + cost + " de mana por uso)");
        }

        @Override
        public void onGive(LivingEntity entity, DamageResult damageResult) {
            ItemStack stack = user().base().getItemInHand();

            if (skill.getUser().iContainer().protection().canUseSkill(this.skill.getType()) && skill.takeMana()) {

                if (stack != null) {
                    Material material = stack.getType();
                    if (material == Material.DIAMOND_SWORD
                            || material == Material.GOLDEN_SWORD
                            || material == Material.STONE_SWORD
                            || material == Material.IRON_SWORD
                            || material == Material.WOODEN_SWORD) {
                        damageResult.add(extra);
                        return;
                    }
                }

                damageResult.add(extra / 2d);
            }
        }
    }
}
