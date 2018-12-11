package com.github.manolo8.simplecraft.module.skill.types;

import com.github.manolo8.simplecraft.interfaces.Tickable;
import com.github.manolo8.simplecraft.module.skill.Level;
import com.github.manolo8.simplecraft.module.skill.Skill;
import com.github.manolo8.simplecraft.module.skill.tools.DisableSkill;
import com.github.manolo8.simplecraft.module.user.UserService;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_13_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class SkillAbsorption extends Skill<Level> implements DisableSkill {

    private static Level[] levels;


    static {
        levels = new Level[16];

        levels[0] = new Beginner(1, 1, 1, 1);
        levels[1] = new Beginner(10, 1, 2, 1);
        levels[2] = new Beginner(20, 1, 3, 1);
        levels[3] = new Beginner(30, 2, 4, 1);
        levels[4] = new Beginner(40, 3, 4, 2);
        levels[5] = new Beginner(50, 4, 5, 2);
        levels[6] = new Beginner(60, 5, 6, 2);
        levels[7] = new Beginner(70, 5, 7, 2);
        levels[8] = new Beginner(80, 6, 8, 2);
        levels[9] = new Beginner(90, 6, 9, 2);
        levels[10] = new Beginner(100, 7, 10, 3);
        levels[11] = new Beginner(110, 7, 12, 3);
        levels[12] = new Beginner(120, 8, 14, 3);
        levels[13] = new Beginner(130, 8, 16, 3);
        levels[14] = new Beginner(140, 9, 18, 3);
        levels[15] = new Beginner(150, 10, 20, 4);
    }

    public SkillAbsorption() {
        super(levels, "Escudo", 4, Material.GOLDEN_APPLE);
    }

    @Override
    public Skill newInstance() {
        return new SkillAbsorption();
    }

    @Override
    public void onDisable() {
        ((CraftPlayer) getUser().base()).getHandle().setAbsorptionHearts(0);
    }

    static class Beginner extends Level implements Tickable {

        private int shield;
        private double regen;

        public Beginner(int upgradeAmount, double cost, int shield, int regen) {
            super(upgradeAmount, cost);
            this.shield = shield;
            this.regen = regen;
            this.info = Arrays.asList("§aEscudo.",
                    "§a(Dá " + shield + " de escudo)",
                    "§aRegenera " + df.format(regen) + " por segundo",
                    "§c(Custa " + cost + " de mana por uso)");
        }

        @Override
        public void tick() {
            if (UserService.tick % 20 != 1) return;

            if (skill.getUser().iContainer().protection().canUseSkill(this.skill.getType())) {
                Player base = user().base();

                float absorption = ((CraftPlayer) base).getHandle().getAbsorptionHearts();

                if (absorption == shield) return;

                if (!skill.takeMana()) return;

                absorption += regen / 2D;

                if (absorption > shield) absorption = shield;

                ((CraftPlayer) base).getHandle().setAbsorptionHearts(absorption);
            }

        }
    }
}
