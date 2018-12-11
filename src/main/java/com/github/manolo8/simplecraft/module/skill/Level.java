package com.github.manolo8.simplecraft.module.skill;

import com.github.manolo8.simplecraft.module.user.User;

import java.util.List;

public class Level<T extends Skill> implements Cloneable {

    protected List<String> info;
    protected T skill;
    private int upgradeAmount;
    private double cost;

    public Level(int upgradeAmount, double cost) {
        this.upgradeAmount = upgradeAmount;
        this.cost = cost;
    }

    public User user() {
        return skill.getUser();
    }

    public List<String> getInfo() {
        return info;
    }

    public int getUpgradeAmount() {
        return upgradeAmount;
    }

    public double getCost() {
        return cost;
    }

    public void setSkill(T skill) {
        this.skill = skill;
    }

    @Override
    public Level clone() {
        try {
            return (Level) super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }
}
