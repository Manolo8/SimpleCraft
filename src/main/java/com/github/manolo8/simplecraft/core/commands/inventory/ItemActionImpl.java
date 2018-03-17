package com.github.manolo8.simplecraft.core.commands.inventory;

import org.bukkit.inventory.ItemStack;

public class ItemActionImpl implements ItemAction {

    private int index;
    private ItemStack itemStack;
    private Action action;

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public void setItemStack(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }
}