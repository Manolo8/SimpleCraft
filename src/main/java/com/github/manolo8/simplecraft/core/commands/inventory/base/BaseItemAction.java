package com.github.manolo8.simplecraft.core.commands.inventory.base;

import com.github.manolo8.simplecraft.core.commands.inventory.Action;
import com.github.manolo8.simplecraft.core.commands.inventory.InventoryView;
import com.github.manolo8.simplecraft.core.commands.inventory.ItemAction;
import org.bukkit.inventory.ItemStack;

public class BaseItemAction extends ItemAction {

    protected int index;
    protected ItemStack itemStack;
    protected Action action;
    protected InventoryView view;

    public BaseItemAction() {
    }

    public BaseItemAction(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    public static BaseItemAction of(ItemStack item) {
        return new BaseItemAction(item);
    }

    public int index() {
        return index;
    }

    public BaseItemAction setIndex(int index) {
        this.index = index;

        return this;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public BaseItemAction setItem(ItemStack itemStack) {
        this.itemStack = itemStack;

        return this;
    }

    public Action getAction() {
        return action;
    }

    public BaseItemAction setAction(Action action) {
        this.action = action;
        return this;
    }
}