package com.github.manolo8.simplecraft.module.machine.type.drop;

import com.github.manolo8.simplecraft.core.data.model.base.BaseEntity;
import org.bukkit.inventory.ItemStack;

public class MachineDrop extends BaseEntity {

    private int typeId;
    private ItemStack item;
    private int rarity;

    public int getTypeId() {
        return typeId;
    }

    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }

    public ItemStack getItem() {
        return item;
    }

    public void setItem(ItemStack item) {
        this.item = item;
    }

    public int getRarity() {
        return rarity;
    }

    public void setRarity(int rarity) {
        this.rarity = rarity;
    }
}
