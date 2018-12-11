package com.github.manolo8.simplecraft.tools.item;

import com.github.manolo8.simplecraft.core.data.model.base.BaseEntity;
import org.bukkit.inventory.ItemStack;

public class Item extends BaseEntity {

    private ItemStack itemStack;
    private int hash;

    public ItemStack get() {
        return itemStack;
    }

    public ItemStack copy() {
        return itemStack.clone();
    }

    public int getHash() {
        return hash;
    }

    public int getType() {
        return itemStack.getType().getId();
    }

    public void setHash(int hash) {
        this.hash = hash;
    }

    public void set(ItemStack itemStack) {
        this.itemStack = itemStack;
        this.hash = itemStack.hashCode();
    }
}
