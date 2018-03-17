package com.github.manolo8.simplecraft.domain.user;

import com.github.manolo8.simplecraft.core.commands.inventory.InventoryView;
import com.github.manolo8.simplecraft.core.commands.inventory.View;
import com.github.manolo8.simplecraft.core.protection.Protection;
import com.github.manolo8.simplecraft.core.protection.ProtectionChecker;
import com.github.manolo8.simplecraft.core.world.WorldService;
import com.github.manolo8.simplecraft.data.model.NamedEntity;
import com.github.manolo8.simplecraft.data.model.PositionEntity;
import com.github.manolo8.simplecraft.domain.group.Group;
import com.github.manolo8.simplecraft.domain.plot.data.PlotInfo;
import com.github.manolo8.simplecraft.domain.warp.Warp;
import com.github.manolo8.simplecraft.utils.location.SimpleLocation;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class User extends NamedEntity {

    private UUID uuid;
    private int worldId;
    private Player base;
    private Group group;
    private double money;

    private ProtectionChecker currentChecker;
    private Protection protection;
    private InventoryView inventoryView;

    private SimpleLocation pos1;
    private SimpleLocation pos2;
    private List<PlotInfo> plots;

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public int getWorldId() {
        return worldId;
    }

    public void setWorldId(int worldId) {
        this.worldId = worldId;
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

    public SimpleLocation getPos1() {
        return pos1;
    }

    public void setPos1(SimpleLocation pos1) {
        this.pos1 = pos1;
    }

    public SimpleLocation getPos2() {
        return pos2;
    }

    public void setPos2(SimpleLocation pos2) {
        this.pos2 = pos2;
    }

    public List<PlotInfo> getPlots() {
        return plots;
    }

    public void setPlots(List<PlotInfo> plots) {
        this.plots = plots;
    }

    @Override
    public void addReference() {
        super.addReference();
        getGroup().addReference();
    }

    @Override
    public void removeReference() {
        super.removeReference();
        getGroup().removeReference();
    }

    //Utils
    public void sendMessage(Object object) {
        base.sendMessage(object.toString());
    }

    public void teleport(Location location) {
        getBase().teleport(location);
    }

    public void teleport(PositionEntity p) {
        World world = WorldService.instance.getWorldByWorldId(p.getWorldId());
        int x = (p.getX() * 64) + 40;
        int z = (p.getZ() * 64) + 15;

        teleport(new Location(world, x, 65, z));
    }

    public void teleport(Warp warp) {
        World world = WorldService.instance.getWorldByWorldId(warp.getWorldId());

        teleport(warp.getLocation().getLocation(world));
    }

    public double distance(User target) {
        if (base == null || target.base == null) return -1;
        Location loc1 = base.getLocation();
        Location loc2 = target.base.getLocation();
        if (!loc1.getWorld().equals(loc2.getWorld())) return -1;
        return loc1.distance(loc2);
    }

    public void playSound(Sound sound, float v, float v1) {
        base.playSound(base.getLocation(), sound, v, v1);
    }
    //Utils

    //Money
    public void deposit(double quantity) {
        setNeedSave(true);
        this.money += quantity;
    }

    public boolean withdraw(double quantity) {
        if (this.money >= quantity) {
            this.money -= quantity;
            setNeedSave(true);
            return true;
        }
        return false;
    }

    public double getMoney() {
        return money;
    }

    public void setMoney(double money) {
        setNeedSave(true);
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
        setNeedSave(true);
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

    //Inventory view
    public void createView(View view) {
        InventoryView creation = new InventoryView();
        creation.setUser(this);
        setInventoryView(creation);
        creation.open();
        creation.addView(view);
        getBase().updateInventory();
    }

    public InventoryView getInventoryView() {
        return inventoryView;
    }

    public void setInventoryView(InventoryView inventoryView) {
        this.inventoryView = inventoryView;
    }
    //Inventory view
}
