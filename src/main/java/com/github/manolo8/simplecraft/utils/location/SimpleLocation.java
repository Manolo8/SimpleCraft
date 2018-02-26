package com.github.manolo8.simplecraft.utils.location;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.UUID;

public class SimpleLocation implements Cloneable {

    private int x;
    private int y;
    private int z;

    public SimpleLocation(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    public SimpleLocation(Location location) {
        this.x = (int) location.getX();
        this.y = (int) location.getY();
        this.z = (int) location.getZ();
    }

    public static SimpleLocation fromString(String string) {
        try {
            String[] pos = string.split(" ");
            return new SimpleLocation(Integer.valueOf(pos[0]), Integer.valueOf(pos[1]), Integer.valueOf(pos[2]));
        } catch (Exception e) {
            return null;
        }
    }


    public static SimpleLocation mathMax(SimpleLocation one, SimpleLocation two) {
        return new SimpleLocation(Math.max(one.x, two.x),
                Math.max(one.y, two.y),
                Math.max(one.z, two.z));
    }

    public static SimpleLocation mathMin(SimpleLocation one, SimpleLocation two) {
        return new SimpleLocation(Math.min(one.x, two.x),
                Math.min(one.y, two.y),
                Math.min(one.z, two.z));
    }

    public Block getBlock(UUID world) {
        return getBlock(Bukkit.getWorld(world));
    }

    public Block getBlock(World world) {
        return world.getBlockAt(x, y, z);
    }

    public SimpleLocation add(int x, int y, int z) {
        return new SimpleLocation(this.x + x, this.y + y, this.z + z);
    }

    public SimpleLocation add(int x, int z) {
        return new SimpleLocation(this.x + x, this.y, this.z + z);
    }

    @Override
    public String toString() {
        return x + " " + y + " " + z;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        SimpleLocation location = (SimpleLocation) object;
        return x == location.x &&
                y == location.y &&
                z == location.z;
    }

    public SimpleLocation add(SimpleLocation location) {
        return add(location.x, location.y, location.z);
    }

    public Location getLocation(World world) {
        return new Location(world, x, y, z);
    }
}
