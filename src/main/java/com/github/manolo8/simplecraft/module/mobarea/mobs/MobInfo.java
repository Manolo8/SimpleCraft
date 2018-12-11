package com.github.manolo8.simplecraft.module.mobarea.mobs;

import com.github.manolo8.simplecraft.core.data.model.named.NamedEntity;
import com.github.manolo8.simplecraft.module.mobarea.mobs.item.MobDrop;
import com.github.manolo8.simplecraft.module.mobarea.mobs.item.MobDropRepository;
import com.github.manolo8.simplecraft.module.user.User;
import com.github.manolo8.simplecraft.utils.def.StringUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;

import java.sql.SQLException;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class MobInfo extends NamedEntity {

    private final Random random = new Random();
    private final MobDropRepository dropRepository;
    private Mob mob;

    private String displayName;
    private int life;
    private int range;
    private int damage;
    private double speed;
    private int exp;

    private List<MobDrop> drops;

    private double maxChance;
    private int quantity;
    private int currentQuantity;
    private int maxQuantity;

    public MobInfo(MobDropRepository dropRepository) {
        this.dropRepository = dropRepository;
    }

    //======================================================
    //=====================ENCAPSULATION====================
    //======================================================
    public Mob getMob() {
        return mob;
    }

    public void setMob(Mob mob) {
        this.mob = mob;
    }


    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
        modified();
    }

    public int getLife() {
        return life;
    }

    public void setLife(int life) {
        this.life = life;
        modified();
    }

    public int getRange() {
        return range;
    }

    public void setRange(int range) {
        this.range = range;
        modified();
    }

    public int getDamage() {
        return damage;
    }

    public void setDamage(int damage) {
        this.damage = damage;
        modified();
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
        modified();
    }

    public List<MobDrop> getDrops() {
        return drops;
    }

    public void setDrops(List<MobDrop> drops) {
        this.drops = drops;
        recalculate();
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getMaxQuantity() {
        return maxQuantity;
    }

    public void setMaxQuantity(int maxQuantity) {
        this.maxQuantity = maxQuantity;
        modified();
    }

    public int getExp() {
        return exp;
    }

    public void setExp(int exp) {
        this.exp = exp;
        modified();
    }
    //======================================================
    //====================_ENCAPSULATION====================
    //======================================================


    //======================================================
    //=======================METHODS========================
    //======================================================
    public void addDrop(ItemStack item, double chance) throws SQLException {
        drops.add(dropRepository.create(this, item, chance));

        recalculate();
    }

    public MobDrop getDrop(ItemStack item) {
        for (MobDrop Item : drops)
            if (Item.getItem().isSimilar(item))
                return Item;

        return null;
    }

    public MobDrop getDrop(String item) {
        for (MobDrop Item : drops)
            if (Item.getItem().getType().name().equals(item))
                return Item;

        return null;
    }

    public boolean removeDrop(MobDrop drop) {
        drops.remove(drop);
        drop.remove();
        recalculate();

        return false;
    }

    public void recalculate() {
        drops.sort(Comparator.comparingDouble(MobDrop::getChance));

        maxChance = 0;

        for (MobDrop drop : drops) {
            maxChance += drop.getChance();
            drop.setCalculatedChance(maxChance);
        }
    }

    public MobDrop next() {
        double rnd = random.nextDouble();

        for (MobDrop drop : drops)
            if (drop.getCalculatedChance() >= rnd)
                return drop;

        return null;
    }

    public double availableChance() {
        double chance = 0;

        for (MobDrop drop : drops) chance += drop.getChance();

        return 1 - chance;
    }

    public void changeName(String newName) {
        setName(StringUtils.removeColors(newName).replaceAll(" ", ""));
        setDisplayName(newName);
    }

    public boolean match(LivingEntity entity) {
        return entity.getCustomName() != null && entity.getCustomName().startsWith(displayName);
    }

    private void updateAttribute(AttributeInstance instance, double value) {
        if (instance != null && value != 0) instance.setBaseValue(value);
    }

    public void reset(double loaded) {
        this.currentQuantity = 0;
        this.quantity = (int) (maxQuantity * loaded);
    }

    public void addCurrent() {
        currentQuantity++;
    }

    public boolean overflow() {
        return currentQuantity > quantity;
    }

    public int getMissing() {
        return quantity - currentQuantity;
    }

    public void spawn(Location location) {
        LivingEntity creature = mob.create(location);

        updateAttribute(creature.getAttribute(Attribute.GENERIC_MAX_HEALTH), life);
        updateAttribute(creature.getAttribute(Attribute.GENERIC_FOLLOW_RANGE), range);
        updateAttribute(creature.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE), damage);
        updateAttribute(creature.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED), speed);

        if (life != 0) creature.setHealth(life);

        creature.setCustomName(displayName);
        creature.setCustomNameVisible(true);
    }

    public void death(User killer, List<ItemStack> drops) {
        killer.skill().giveExp(exp);

        ItemStack hand = killer.base().getInventory().getItemInMainHand();
        int loot = hand.getEnchantmentLevel(Enchantment.LOOT_BONUS_MOBS);
        boolean fire = hand.getEnchantmentLevel(Enchantment.FIRE_ASPECT) > 0;

        do {
            MobDrop drop = next();
            if (drop != null) {
                drops.add(fire ? checkCook(drop.getItem()) : drop.getItem());
            }
        } while (loot != 0 && random.nextInt(loot) != 0);
    }

    private ItemStack checkCook(ItemStack item) {
        switch (item.getType()) {
            case MUTTON:
                return new ItemStack(Material.COOKED_MUTTON, item.getAmount());
            case CHICKEN:
                return new ItemStack(Material.COOKED_CHICKEN, item.getAmount());
            case BEEF:
                return new ItemStack(Material.COOKED_BEEF, item.getAmount());
            case PORKCHOP:
                return new ItemStack(Material.COOKED_PORKCHOP, item.getAmount());
            default:
                return item;
        }
    }
    //======================================================
    //======================_METHODS========================
    //======================================================


    //======================================================
    //=======================ENTITY=========================
    //======================================================
    @Override
    public void remove() {
        super.remove();
        for (MobDrop drop : drops) drop.remove();
    }
    //======================================================
    //======================_ENTITY=========================
    //======================================================
}