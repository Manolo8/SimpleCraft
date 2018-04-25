package com.github.manolo8.simplecraft.modules.skill.types;

import com.github.manolo8.simplecraft.modules.skill.MagicLevel;
import com.github.manolo8.simplecraft.modules.skill.Skill;
import com.github.manolo8.simplecraft.modules.skill.SkillMagic;
import com.github.manolo8.simplecraft.modules.skill.tools.Interactable;
import com.github.manolo8.simplecraft.modules.user.User;
import com.github.manolo8.simplecraft.utils.ItemStackUtils;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class SkillMagicBigJump extends SkillMagic {

    private static MagicLevel[] levels;

    static {
        levels = new MagicLevel[10];

        levels[0] = new Beginner(1, 1, 10);
        levels[1] = new Beginner(3, 1, 9);
        levels[2] = new Beginner(5, 2, 8);
        levels[3] = new Beginner(7, 2, 7);
        levels[4] = new Beginner(9, 2, 7);
        levels[5] = new Beginner(11, 3, 6);
        levels[6] = new Beginner(13, 3, 6);
        levels[7] = new Beginner(15, 4, 5);
        levels[8] = new Beginner(18, 4, 5);
        levels[9] = new Beginner(23, 5, 4);
    }

    public SkillMagicBigJump() {
        super(levels, "Grande pulo", 4, Material.RABBIT_STEW, ItemStackUtils.create(Material.STICK, "§aGrande pulo"));
    }

    @Override
    public Skill newInstance() {
        return new SkillMagicBigJump();
    }

    static class Beginner extends MagicLevel {

        public Beginner(int upgradeAmount, int max, int cooldown) {
            super(upgradeAmount, max, cooldown);
            this.info = new String[]{"§aVarinha do grande pulo.",
                    "§a(Pula até " + max + ")"};
        }

        @Override
        public void handle(User user) {
            user.getBase().setVelocity(user.getBase().getLocation().getDirection().normalize().multiply(2));
            user.playSound(Sound.ENTITY_SLIME_JUMP, 20, 20);
        }
    }

}
