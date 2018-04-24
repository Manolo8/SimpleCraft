package com.github.manolo8.simplecraft.modules.skill.types;

import com.github.manolo8.simplecraft.modules.skill.Level;
import com.github.manolo8.simplecraft.modules.skill.Skill;
import com.github.manolo8.simplecraft.modules.skill.tools.JoinSkill;
import com.github.manolo8.simplecraft.modules.user.User;

import java.util.Random;

public class SkillExtraLife extends Skill {

    private static Level[] levels;
    private static Random random;

    static {
        levels = new Level[10];

        levels[0] = new Beginner(1, 2);
        levels[1] = new Beginner(3, 8);
        levels[2] = new Beginner(6, 18);
        levels[3] = new Beginner(10, 25);
        levels[4] = new Beginner(15, 30);
        levels[5] = new Beginner(25, 35);
        levels[6] = new Beginner(27, 43);
        levels[7] = new Beginner(30, 50);
        levels[8] = new Beginner(35, 59);
        levels[9] = new Beginner(40, 70);
    }

    public SkillExtraLife(Random rnd) {
        random = rnd;
    }

    @Override
    public int getType() {
        return 2;
    }

    @Override
    public Level getLevelHandler() {
        return levels[level];
    }

    @Override
    public boolean hasNextLevel() {
        return levels.length > level;
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

        }
    }
}
