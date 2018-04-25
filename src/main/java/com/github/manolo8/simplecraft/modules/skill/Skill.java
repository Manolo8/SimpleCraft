package com.github.manolo8.simplecraft.modules.skill;

import com.github.manolo8.simplecraft.data.model.BaseEntity;
import org.bukkit.Material;

import java.util.Random;

public abstract class Skill<T extends Level> extends BaseEntity implements Cloneable {

    protected static Random random = new Random();
    protected T[] levels;
    protected int level;

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
        setNeedSave(true);

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

    public Material getMaterial() {
        return material;
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
        return "§a" + name + " (" + level + ")";
    }

    public String getUpgradeDisplay() {
        if (!hasNextLevel()) return "§aCompleto :)";
        else {
            Level next = levels[handlerId + 1];
            return "§a" + level + "/" + next.getUpgradeAmount() + " para o próximo level!";
        }
    }

    public int getType() {
        return type;
    }

    public T getLevelHandler() {
        return handler;
    }

    public boolean hasNextLevel() {
        return levels.length > handlerId + 1;
    }

    public abstract Skill newInstance();
}