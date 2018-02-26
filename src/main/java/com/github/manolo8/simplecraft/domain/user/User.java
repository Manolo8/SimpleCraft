package com.github.manolo8.simplecraft.domain.user;

import com.github.manolo8.simplecraft.domain.group.Group;
import com.github.manolo8.simplecraft.core.protection.Protection;
import com.github.manolo8.simplecraft.core.protection.ProtectionChecker;
import com.github.manolo8.simplecraft.model.NamedEntity;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.UUID;

public class User extends NamedEntity {

    private UUID uuid;
    private Player base;
    private Group group;
    private double money;

    private ProtectionChecker currentChecker;
    private Protection protection;

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public boolean match(Player player) {
        return uuid.equals(player.getUniqueId());
    }

    public Player getBase() {
        return base;
    }

    public void setBase(Player base) {
        this.base = base;
    }

    //Utils
    public void sendMessage(Object object) {
        base.sendMessage(object.toString());
    }

    public double distance(User target) {
        if (base == null || target.base == null) return -1;
        Location loc1 = base.getLocation();
        Location loc2 = target.base.getLocation();
        if (!loc1.getWorld().equals(loc2.getWorld())) return -1;
        return loc1.distance(loc2);
    }
    //Utils

    //Money
    public void deposit(double quantity) {
        this.money += quantity;
    }

    public boolean withDraw(double quantity) {
        if (this.money >= quantity) {
            this.money -= quantity;
            return true;
        }
        return false;
    }

    public double getMoney() {
        return money;
    }

    public void setMoney(double money) {
        this.money = money;
    }
    //Money

    //Permission
    public boolean hasPermission(String permission) {
        return (base != null && base.isOp() || group != null && group.hasPermission(permission));
    }

    public Group getGroup() {
        return this.group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }
    //Permission

    //Protection
    public Protection getProtection() {
        return protection;
    }

    public void setProtection(Protection protection) {
        this.protection = protection;
    }

    public ProtectionChecker getCurrentChecker() {
        return currentChecker;
    }

    public void setCurrentChecker(ProtectionChecker currentChecker) {
        this.currentChecker = currentChecker;
    }

    //Protection
}
