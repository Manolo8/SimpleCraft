package com.github.manolo8.simplecraft.module.mobarea.mobs.item;

import com.github.manolo8.simplecraft.core.data.model.base.BaseEntity;
import org.bukkit.inventory.ItemStack;

public class MobDrop extends BaseEntity {

    private ItemStack item;
    private double chance;
    private double calculatedChance;

    public ItemStack getItem() {
        return item;
    }

    public void setItem(ItemStack item) {
        this.item = item;
    }

    public double getChance() {
        return chance;
    }

    public void setChance(double chance) {
        this.chance = chance;
        modified();
    }

    public double getCalculatedChance() {
        return calculatedChance;
    }

    public void setCalculatedChance(double calculatedChance) {
        this.calculatedChance = calculatedChance;
    }
}
