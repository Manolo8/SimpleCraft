package com.github.manolo8.simplecraft.modules.skill.types;

import com.github.manolo8.simplecraft.modules.skill.Level;
import com.github.manolo8.simplecraft.modules.skill.Skill;
import com.github.manolo8.simplecraft.modules.skill.tools.JoinSkill;
import com.github.manolo8.simplecraft.modules.user.User;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;

public class SkillExtraLife extends Skill<Level> {

    private static Level[] levels;

    static {
        levels = new Level[10];

        levels[0] = new Beginner(1, 3);
        levels[1] = new Beginner(3, 7);
        levels[2] = new Beginner(9, 11);
        levels[3] = new Beginner(15, 15);
        levels[4] = new Beginner(21, 20);
        levels[5] = new Beginner(29, 24);
        levels[6] = new Beginner(35, 28);
        levels[7] = new Beginner(42, 31);
        levels[8] = new Beginner(49, 36);
        levels[9] = new Beginner(55, 40);
    }

    public SkillExtraLife() {
        super(levels, "Vida extra", 2, Material.SPECKLED_MELON);
    }

    @Override
    public Skill newInstance() {
        return new SkillExtraLife();
    }

    static class Beginner extends Level implements JoinSkill {

        private int extra;

        public Beginner(int upgradeAmount, int extra) {
            super(upgradeAmount);
            this.extra = extra;
            this.info = new String[]{"§aDá vida extra.",
                    "§a(Aumenta a vida em " + extra + ")"};
        }

        @Override
        public void onJoin(User user) {
            user.getBase()
                    .getAttribute(Attribute.GENERIC_MAX_HEALTH)
                    .setBaseValue(20 + extra);
        }
    }
}
