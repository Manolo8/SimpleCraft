package com.github.manolo8.simplecraft.module.skill.types;

import com.github.manolo8.simplecraft.module.skill.MagicLevel;
import com.github.manolo8.simplecraft.module.skill.Skill;
import com.github.manolo8.simplecraft.module.skill.SkillMagic;
import com.github.manolo8.simplecraft.utils.mc.ItemStackUtils;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class SkillMagicBigJump extends SkillMagic {

    private static MagicLevel[] levels;

    static {
        levels = new MagicLevel[8];

        levels[0] = new Beginner(1, 25, 1, 10);
        levels[1] = new Beginner(5, 25, 1, 9);
        levels[2] = new Beginner(10, 25, 2, 8);
        levels[3] = new Beginner(15, 35, 2, 7);
        levels[4] = new Beginner(20, 35, 2, 7);
        levels[5] = new Beginner(25, 35, 3, 6);
        levels[6] = new Beginner(30, 40, 3, 5);
        levels[7] = new Beginner(35, 40, 4, 4);
    }

    public SkillMagicBigJump() {
        super(levels, "Grande pulo", 6, Material.RABBIT_STEW, ItemStackUtils.create(Material.STICK, "§aGrande pulo"));
    }

    @Override
    public Skill newInstance() {
        return new SkillMagicBigJump();
    }

    static class Beginner extends MagicLevel {

        public Beginner(int upgradeAmount, int cost, int max, int cooldown) {
            super(upgradeAmount, cost, max, cooldown);
            this.info = Arrays.asList("§aVarinha do grande pulo.",
                    "§a(Pula até " + max + ")",
                    "§c(Custa " + cost + " de mana por uso)");
        }

        @Override
        public void handle() {
            Player base = user().base();
            base.setVelocity(base.getLocation().getDirection().normalize().multiply(2));
            user().playSound(Sound.ENTITY_SLIME_JUMP, 20, 20, false);
        }
    }

}
