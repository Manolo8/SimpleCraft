package com.github.manolo8.simplecraft.data.model;

public class LocationEntity extends BaseEntity {

    protected int x;
    protected int y;
    protected int z;
    protected int worldId;

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

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getZ() {
        return z;
    }

    public void setZ(int z) {
        this.z = z;
    }

    public boolean match(int x, int y, int z, int worldId) {
        return this.x == x && this.y == y && this.z == z && this.worldId == worldId;
    }
}
