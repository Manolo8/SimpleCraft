package com.github.manolo8.simplecraft.module.kit;

import com.github.manolo8.simplecraft.core.data.model.named.NamedEntity;
import com.github.manolo8.simplecraft.module.kit.item.KitItem;
import com.github.manolo8.simplecraft.module.kit.item.KitItemRepository;
import com.github.manolo8.simplecraft.module.user.User;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.sql.SQLException;
import java.util.List;

public class Kit extends NamedEntity {

    private final KitItemRepository repository;
    private long delay;
    private List<KitItem> items;
    private int slot;
    private int rank;

    public Kit(KitItemRepository repository) {
        this.repository = repository;
    }

    //======================================================
    //=====================ENCAPSULATION====================
    //======================================================
    public long getDelay() {
        return delay;
    }

    public void setDelay(long delay) {
        this.delay = delay;
        modified();
    }

    public List<KitItem> getItems() {
        return items;
    }

    public void setItems(List<KitItem> items) {
        this.items = items;
    }

    public int getSlot() {
        return slot;
    }

    public void setSlot(int slot) {
        this.slot = slot;
        this.modified();
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
        modified();
    }

    //======================================================
    //====================_ENCAPSULATION====================
    //======================================================


    //======================================================
    //========================METHODS=======================
    //======================================================
    public void addItem(ItemStack item) throws SQLException {
        this.items.add(repository.create(item, this));
    }

    public boolean canUse(User user) {
        return user.hasPermission("simplecraft.kits." + getFastName())
                && user.rank().get() >= getRank();
    }

    public void clearItems() {
        for (KitItem item : items) {
            item.remove();
        }

        items.clear();
    }

    public void giveTo(User user) {
        Inventory inventory = user.base().getInventory();

        for (KitItem item : items)
            inventory.addItem(item.getItem());
    }

    @Override
    public void remove() {
        super.remove();

        for (KitItem item : items)
            item.remove();
    }
    //======================================================
    //=======================_METHODS=======================
    //======================================================
}
