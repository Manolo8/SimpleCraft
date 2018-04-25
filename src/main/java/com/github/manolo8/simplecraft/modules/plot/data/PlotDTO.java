package com.github.manolo8.simplecraft.modules.plot.data;

import java.util.List;

public class PlotDTO {

    private int id;
    private int worldId;
    private int x;
    private int z;
    private int owner;
    private List<Integer> friends;
    private boolean pvpOn;
    private boolean pvpAnimalOn;
    private double sellPrice;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getWorldId() {
        return worldId;
    }

    public void setWorldId(int worldId) {
        this.worldId = worldId;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getZ() {
        return z;
    }

    public void setZ(int z) {
        this.z = z;
    }

    public int getOwner() {
        return owner;
    }

    public void setOwner(int owner) {
        this.owner = owner;
    }

    public List<Integer> getFriends() {
        return friends;
    }

    public void setFriends(List<Integer> friends) {
        this.friends = friends;
    }

    public boolean isPvpOn() {
        return pvpOn;
    }

    public void setPvpOn(boolean pvpOn) {
        this.pvpOn = pvpOn;
    }

    public boolean isPvpAnimalOn() {
        return pvpAnimalOn;
    }

    public void setPvpAnimalOn(boolean pvpAnimalOn) {
        this.pvpAnimalOn = pvpAnimalOn;
    }

    public double getSellPrice() {
        return sellPrice;
    }

    public void setSellPrice(double sellPrice) {
        this.sellPrice = sellPrice;
    }
}
