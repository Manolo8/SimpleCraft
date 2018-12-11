package com.github.manolo8.simplecraft.module.skill;

import com.github.manolo8.simplecraft.interfaces.Tickable;
import com.github.manolo8.simplecraft.module.skill.tools.DisableSkill;
import com.github.manolo8.simplecraft.utils.mc.InventoryUtils;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public abstract class SkillMagic extends Skill<MagicLevel> implements Tickable, DisableSkill {

    protected int available;
    protected int cooldown;
    protected int max;
    protected long lastUse;
    protected ItemStack wand;
    protected int wandSlotId;

    public SkillMagic(MagicLevel[] levels, String name, int type, Material material, ItemStack wand) {
        super(levels, name, type, material);
        this.wand = wand;
    }

    @Override
    public void setLevel(int level) {
        super.setLevel(level);
        max = levels[handlerId].getMax();
        cooldown = levels[handlerId].getCooldown();
    }

    public boolean canUse() {
        return (available != 0);
    }

    public void use() {
        if (available == 0) return;
        available--;
    }

    public void updateWand() {
        ItemStack item = getWandItem();
        if (item != null) {

            ItemMeta meta = item.getItemMeta();
            meta.setLore(Arrays.asList("§a" + available + "/" + max, "§aRecarga de " + cooldown + "s"));
            item.setItemMeta(meta);

            if (available == 0)
                item.removeEnchantment(Enchantment.DURABILITY);
            else {
                item.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
                item.setAmount(available);
            }
        }
    }

    public void giveWand() {
        InventoryUtils.addItemTo(getUser().base().getInventory(), wand);
        updateWand();
    }

    public ItemStack getWand() {
        return wand;
    }

    public ItemStack getWandItem() {
        PlayerInventory inventory = getUser().base().getInventory();

        ItemStack item = inventory.getItem(wandSlotId);

        if (isWand(item)) return item;
        else for (int i = 0; i < inventory.getSize(); i++) {
            item = inventory.getItem(i);
            if (isWand(item)) {
                wandSlotId = i;
                return item;
            }
        }

        return null;
    }

    public boolean hasWand() {
        PlayerInventory inventory = getUser().base().getInventory();

        if (isWand(inventory.getItem(wandSlotId))) return true;
        else for (int i = 0; i < inventory.getSize(); i++) {
            if (isWand(inventory.getItem(i))) {
                wandSlotId = i;
                return true;
            }
        }

        return false;
    }

    public boolean isWand(ItemStack itemStack) {
        if (itemStack == null) return false;
        if (itemStack.getType() != Material.STICK) return false;

        ItemMeta meta = itemStack.getItemMeta();

        if (meta.getDisplayName() == null) return false;

        return (meta.getDisplayName().endsWith(name));
    }

    @Override
    public void tick() {
        int lastAvailable = available;
        long time = System.currentTimeMillis() - lastUse;

        double floor = Math.floor(time / (cooldown * 1000));

        if (available + floor >= max) {
            lastUse = System.currentTimeMillis();
            available = max;
        } else {
            available += floor;
            lastUse += floor * (cooldown * 1000);
        }

        if (lastAvailable != available) {
            updateWand();
        }
    }

    @Override
    public void onDisable() {
        ItemStack[] stacks = owner.user().base().getInventory().getContents();

        for (ItemStack stack : stacks) {
            if (isWand(stack)) stack.setAmount(0);
        }
    }
}
