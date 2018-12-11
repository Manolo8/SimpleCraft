package com.github.manolo8.simplecraft.module.kit.item;

import com.github.manolo8.simplecraft.core.data.model.base.BaseEntity;
import org.bukkit.inventory.ItemStack;

public class KitItem extends BaseEntity {

    private ItemStack item;

    public ItemStack getItem() {
        return item;
    }

    public void setItem(ItemStack item) {
        this.item = item;
    }
}
