package com.github.manolo8.simplecraft.modules.plot;

import com.github.manolo8.simplecraft.core.protection.Protection;
import com.github.manolo8.simplecraft.data.model.PositionEntity;
import com.github.manolo8.simplecraft.modules.plot.data.PlotInfo;
import com.github.manolo8.simplecraft.modules.user.User;
import org.bukkit.Location;
import org.bukkit.Material;

import java.util.List;

public class Plot extends PositionEntity implements Protection {

    private final PlotInfo info;
    private int owner;
    private List<Integer> friends;
    private boolean pvpOn;
    private boolean pvpAnimalOn;
    private double sellPrice;
    private int users;

    public Plot(PlotInfo info) {
        this.info = info;
    }

    //---- ENCAPSULATION ----
    @Override
    public int getUsers() {
        return users;
    }

    @Override
    public void addUser() {
        users++;
    }

    @Override
    public void removeUser() {
        users--;
    }

    public int getOwner() {
        return owner;
    }

    public void setOwner(int owner) {
        this.owner = owner;
        info.setOwnerId(owner);
        setNeedSave(true);
    }

    public List<Integer> getFriends() {
        return friends;
    }

    public void setFriends(List<Integer> friends) {
        this.friends = friends;
        setNeedSave(true);
    }

    public boolean addFriend(User user) {
        if (friends.contains(user.getId())) return false;
        friends.add(user.getId());
        setNeedSave(true);
        return true;
    }

    public boolean clearFriends() {
        if (friends.isEmpty()) return false;
        friends.clear();
        setNeedSave(true);
        return true;
    }

    public boolean isPvpAnimalOn() {
        return pvpAnimalOn;
    }

    public void setPvpAnimalOn(boolean pvpAnimalOn) {
        this.pvpAnimalOn = pvpAnimalOn;
        setNeedSave(true);
    }

    public double getSellPrice() {
        return sellPrice;
    }

    public void setSellPrice(double sellPrice) {
        this.sellPrice = sellPrice;
        setNeedSave(true);
    }

    public boolean hasPermission(User user) {
        return user.getId() == owner
                || friends.contains(user.getId());
    }

    public PlotInfo getInfo() {
        return info;
    }

    private boolean isStreet(int x, int z) {
        return (z % 4 == 0 || x % 4 == 0);
    }

    //Protection
    @Override
    public boolean isGlobal() {
        return false;
    }
    //---- ENCAPSULATION ----

    @Override
    public boolean canSpread(Material type) {
        return !(type == Material.LAVA || type == Material.FIRE);
    }

    @Override
    public boolean canPistonWork() {
        return false;
    }

    @Override
    public boolean canExplode() {
        return false;
    }

    /**
     * O checker irá usar o método match(x,z) para verificar
     * Por conta da eficiência
     */
    @Override
    public boolean isInArea(Location location) {
        int x = (int) location.getX() >> 4;
        int z = (int) location.getZ() >> 4;

        return !isStreet(x, z) && match(x >> 2, z >> 2);
    }

    @Override
    public boolean canBreak(User user, Material type) {
        return hasPermission(user) || user.hasPermission("plot.ignore.break");
    }

    @Override
    public boolean canPlace(User user, Material type) {
        return hasPermission(user) || user.hasPermission("plot.ignore.place");
    }

    @Override
    public boolean canInteract(User user, Material type) {
        return hasPermission(user) || user.hasPermission("plot.ignore.interact");
    }

    @Override
    public boolean isPvpOn() {
        return pvpOn;
    }

    public void setPvpOn(boolean pvpOn) {
        this.pvpOn = pvpOn;
        setNeedSave(true);
    }

    @Override
    public boolean isAnimalPvpOn(User user) {
        return hasPermission(user) || user.hasPermission("plot.ignore.pvp.animal");
    }
    //Protection
}
