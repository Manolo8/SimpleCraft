package com.github.manolo8.simplecraft.model;

import org.bukkit.World;

import java.util.UUID;

public class PositionEntity extends BaseEntity {

    private World world;
    private int x;
    private int z;

    public World getWorld() {
        return world;
    }

    public void setWorld(World world) {
        this.world = world;
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

    public boolean match(int x, int z, World world) {
        return this.x == x && this.z == z && this.world.equals(world);
    }

    public boolean match(int x, int z, UUID world) {
        return this.x == x && this.z == z && this.world.getUID().equals(world);
    }
}
