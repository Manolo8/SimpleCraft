package com.github.manolo8.simplecraft.module.mine.block.drops;

import com.github.manolo8.simplecraft.core.data.model.base.BaseEntity;
import org.bukkit.inventory.ItemStack;

public class BlockDrop extends BaseEntity {

    private ItemStack drop;
    private double chance;
    private double calculatedChance;

    public ItemStack getDrop() {
        return drop;
    }

    public void setDrop(ItemStack drop) {
        this.drop = drop;
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