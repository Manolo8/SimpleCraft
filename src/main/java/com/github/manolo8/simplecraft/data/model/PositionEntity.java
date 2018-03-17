package com.github.manolo8.simplecraft.data.model;

public class PositionEntity extends BaseEntity {

    protected int x;
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

    public int getZ() {
        return z;
    }

    public void setZ(int z) {
        this.z = z;
    }

    public boolean match(int x, int z) {
        return this.x == x && this.z == z;
    }
}
