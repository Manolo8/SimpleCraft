package com.github.manolo8.simplecraft.utils.location;

import com.github.manolo8.simplecraft.core.world.WorldInfo;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.nio.ByteBuffer;

public class SimpleLocation implements Cloneable {

    protected int x;
    protected int y;
    protected int z;
    protected float yaw;
    protected float pitch;

    public SimpleLocation(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.wrap(bytes);

        this.x = buffer.getInt();
        this.y = buffer.getInt();
        this.z = buffer.getInt();
        this.yaw = buffer.getFloat();
        this.pitch = buffer.getFloat();
    }

    public SimpleLocation(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = 0;
        this.pitch = 0;
    }

    public SimpleLocation(Location location) {
        this.x = (int) location.getX();
        this.y = (int) location.getY();
        this.z = (int) location.getZ();
        this.yaw = location.getYaw();
        this.pitch = location.getPitch();
    }

    public SimpleLocation(int x, int y, int z, int yaw, int pitch) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
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

    //======================================================
    //====================ENCAPSULATION=====================
    //======================================================
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

    public float getYaw() {
        return yaw;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    public float getPitch() {
        return pitch;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }
    //======================================================
    //====================ENCAPSULATION=====================
    //======================================================

    public Block getBlock(World world) {
        return world.getBlockAt(x, y, z);
    }

    public SimpleLocation add(int x, int y, int z) {
        return new SimpleLocation(this.x + x, this.y + y, this.z + z);
    }

    public SimpleLocation add(int x, int z) {
        return new SimpleLocation(this.x + x, this.y, this.z + z);
    }

    public SimpleLocation add(SimpleLocation location) {
        return add(location.x, location.y, location.z);
    }

    public Location getOf(World world) {
        return new Location(world, x, y, z, yaw, pitch);
    }

    public Block getBlock(WorldInfo worldInfo) {
        return getBlock(worldInfo.getWorld());
    }

    public byte[] toBytes() {
        ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES * 3 + Float.BYTES * 2);

        buffer.putInt(x);
        buffer.putInt(y);
        buffer.putInt(z);
        buffer.putFloat(yaw);
        buffer.putFloat(pitch);

        return buffer.array();
    }
}
