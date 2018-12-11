package com.github.manolo8.simplecraft.module.market;

import com.github.manolo8.simplecraft.core.data.model.base.BaseEntity;
import com.github.manolo8.simplecraft.module.money.Money;
import com.github.manolo8.simplecraft.module.user.User;
import com.github.manolo8.simplecraft.utils.mc.InventoryUtils;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class MarketItem extends BaseEntity {

    private Money owner;
    private MarketCategory category;

    private ItemStack item;

    private double cost;
    private long creation;

    //======================================================
    //=====================ENCAPSULATION====================
    //======================================================

    public Money getOwner() {
        return owner;
    }

    public void setOwner(Money owner) {
        this.owner = owner;
    }

    public MarketCategory getCategory() {
        return category;
    }

    public void setCategory(MarketCategory category) {
        this.category = category;
    }

    public ItemStack getItem() {
        return item;
    }

    public void setItem(ItemStack item) {
        this.item = item;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public long getCreation() {
        return creation;
    }

    public void setCreation(long creation) {
        this.creation = creation;
    }

    //======================================================
    //=====================ENCAPSULATION====================
    //======================================================


    //======================================================
    //=======================METHODS========================
    //======================================================
    public boolean buy(User user) {

        if (user.money().hasCoins(cost)) {

            if (addToInventory(user.base().getInventory())) {
                user.money().withdrawCoins(cost);
                owner.depositCoins(cost);
            }

        }

        return false;
    }

    public boolean collect(User user) {
        return addToInventory(user.base().getInventory());
    }

    private boolean addToInventory(Inventory inventory) {
        if (!isRemoved() && !InventoryUtils.isFull(inventory, item)) {
            remove();
            InventoryUtils.addItemTo(inventory, item);
            return true;
        }

        return false;
    }

    //======================================================
    //======================_METHODS========================
    //======================================================

}
