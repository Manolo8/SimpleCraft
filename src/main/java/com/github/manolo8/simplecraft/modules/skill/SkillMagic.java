package com.github.manolo8.simplecraft.modules.skill;

import com.github.manolo8.simplecraft.modules.skill.tools.Tickable;
import com.github.manolo8.simplecraft.modules.user.User;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public abstract class SkillMagic extends Skill<MagicLevel> implements Tickable {

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

    public boolean useIfCan() {
        if (available == 0) return false;
        available--;

        return true;
    }

    private boolean update() {
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

        return lastAvailable != available;
    }

    public void updateWand(User user) {
        PlayerInventory inventory = user.getBase().getInventory();

        ItemStack itemStack = inventory.getItem(wandSlotId);
        if (!isWand(itemStack))
            for (int i = 0; i < inventory.getSize(); i++) {
                itemStack = inventory.getItem(i);
                if (isWand(itemStack)) {
                    wandSlotId = i;
                    break;
                }
            }
        if (!isWand(itemStack)) return;

        ItemMeta meta = itemStack.getItemMeta();
        meta.setLore(Arrays.asList("§a" + available + "/" + max, "§aRecarga de " + cooldown + "s"));
        itemStack.setItemMeta(meta);

        if (available == 0)
            itemStack.removeEnchantment(Enchantment.DURABILITY);
        else  {
            itemStack.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
            itemStack.setAmount(available);
        }
    }

    public void giveWand(User user) {
        user.getBase().getInventory().addItem(wand);
        updateWand(user);
    }

    public ItemStack getWand() {
        return wand;
    }

    public boolean isWand(ItemStack itemStack) {
        if (itemStack == null) return false;
        if (itemStack.getType() != Material.STICK) return false;

        ItemMeta meta = itemStack.getItemMeta();

        return (meta.getDisplayName().endsWith(name));
    }

    @Override
    public void tick(User user) {
        if (update()) updateWand(user);
    }
}
