package com.github.manolo8.simplecraft.domain.warp;

import com.github.manolo8.simplecraft.core.commands.inventory.Action;
import com.github.manolo8.simplecraft.core.commands.inventory.ItemAction;
import com.github.manolo8.simplecraft.data.model.NamedEntity;
import com.github.manolo8.simplecraft.utils.location.SimpleLocation;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class Warp extends NamedEntity implements ItemAction {

    private int index;
    private ItemStack itemStack;
    private int worldId;
    private SimpleLocation location;

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
        setNeedSave(true);
    }

    public int getWorldId() {
        return worldId;
    }

    public void setWorldId(int worldId) {
        this.worldId = worldId;
    }

    public SimpleLocation getLocation() {
        return location;
    }

    public void setLocation(SimpleLocation location) {
        this.location = location;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public void setItemStack(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    public Action getAction() {
        return user -> user.teleport(this);
    }

    public void setMaterial(Material material) {
        this.itemStack.setType(material);
    }

    public void clearLore() {
        ItemMeta meta = itemStack.getItemMeta();
        meta.setLore(null);
        itemStack.setItemMeta(meta);
        setNeedSave(true);
    }

    public void addLore(String line) {
        ItemMeta meta = itemStack.getItemMeta();
        List<String> lore;
        if (meta.hasLore()) lore = meta.getLore();
        else lore = new ArrayList<>();
        lore.add(line);
        meta.setLore(lore);
        itemStack.setItemMeta(meta);
        setNeedSave(true);
    }

    public void setDisplayName(String displayName) {
        ItemMeta meta = itemStack.getItemMeta();
        meta.setDisplayName(displayName);
        itemStack.setItemMeta(meta);
        setNeedSave(true);
    }

}
