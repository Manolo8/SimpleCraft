package com.github.manolo8.simplecraft.module.skill;

import com.github.manolo8.simplecraft.core.data.model.base.BaseEntity;
import com.github.manolo8.simplecraft.module.skill.user.SkillUser;
import com.github.manolo8.simplecraft.module.user.User;
import com.github.manolo8.simplecraft.module.user.identity.Identity;
import org.bukkit.Material;

import java.text.DecimalFormat;
import java.util.Random;

public abstract class Skill<T extends Level> extends BaseEntity implements Cloneable {

    protected static DecimalFormat df = new DecimalFormat("#.#");
    protected static Random random = new Random();

    protected Identity owner;
    protected T[] levels;
    protected int level;
    protected boolean active;

    protected int handlerId;
    protected T handler;

    protected String name;
    protected int type;
    protected Material material;

    public Skill(T[] levels, String name, int type, Material material) {
        this.levels = levels;
        this.type = type;
        this.name = name;
        this.material = material;
        this.handler = levels[0];
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
        modified();

        int last = 0;
        int amount = 0;

        for (int i = 0; i < levels.length; i++) {
            Level handler = levels[i];
            int cost = handler.getUpgradeAmount();

            if (cost > level) continue;
            if (amount > cost) continue;

            last = i;
            amount = handler.getUpgradeAmount();
        }

        this.handlerId = last;
        this.handler = (T) levels[last].clone();
        this.handler.setSkill(this);
    }

    public boolean canTakeMana() {
        return getUser().skill().getMana() >= handler.getCost();
    }

    public boolean takeMana() {
        return getUser().skill().takeMana(handler.getCost());
    }

    public Material getMaterial() {
        return material;
    }

    public int getHandlerId() {
        return handlerId;
    }

    public boolean addLevel() {
        int now = this.handlerId;
        this.setLevel(level + 1);

        return now != this.handlerId;
    }

    public String getName() {
        return name;
    }

    public String getDisplay() {
        return "§a" + name + " (" + (handlerId + 1) + ")";
    }

    public int getMissing() {
        return levels[handlerId + 1].getUpgradeAmount() - level;
    }

    public String getUpgradeDisplay() {
        if (hasNextLevel()) return ("§a" + level + "/"
                + levels[handlerId + 1].getUpgradeAmount()
                + " para o próximo nível!");
        else return "§aCompleto :)";
    }

    public boolean upgrade() {
        SkillUser skillUser = getUser().skill();
        if (hasNextLevel() && skillUser.hasFreePoints()) {
            if (addLevel()) {
                skillUser.cleanupSkills();
            }
            return true;
        }
        return false;
    }

    public int getType() {
        return type;
    }

    public T getLevelHandler() {
        return handler;
    }

    public T getNextHandler() {
        return levels[handlerId + 1];
    }

    public boolean hasNextLevel() {
        return levels.length > handlerId + 1;
    }

    public abstract Skill newInstance();

    public Level[] getLevels() {
        return levels;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
        modified();
    }

    public Identity getOwner() {
        return owner;
    }

    public void setOwner(Identity owner) {
        this.owner = owner;
    }

    public User getUser() {
        return owner.user();
    }
}