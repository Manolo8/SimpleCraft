package com.github.manolo8.simplecraft.core.world.container;

import com.github.manolo8.simplecraft.utils.location.SimpleArea;

public class Area {

    public final int maxX;
    public final int maxY;
    public final int maxZ;
    public final int minX;
    public final int minY;
    public final int minZ;

    public Area(int maxX, int maxY, int maxZ, int minX, int minY, int minZ) {
        this.maxX = maxX;
        this.maxY = maxY;
        this.maxZ = maxZ;
        this.minX = minX;
        this.minY = minY;
        this.minZ = minZ;
    }

    public Area(SimpleArea area) {
        this.maxX = area.getMax().getX();
        this.maxY = area.getMax().getY();
        this.maxZ = area.getMax().getZ();
        this.minX = area.getMin().getX();
        this.minY = area.getMin().getY();
        this.minZ = area.getMin().getZ();
    }

    public Area(int shift, int x, int z) {
        minY = 0;
        maxY = 256;

        int i = 2 << (shift - 1);

        maxZ = z * i + i - 1;
        minZ = z * i;

        maxX = x * i + i - 1;
        minX = x * i;
    }

    public Area add(int maxX, int maxY, int maxZ, int minX, int minY, int minZ) {
        return new Area(this.maxX + maxX,
                this.maxY + maxY,
                this.maxZ + maxZ,
                this.minX + minX,
                this.minY + minY,
                this.minZ + minZ
        );
    }

    /**
     * @param o to test
     * @return true case this container intersects the other
     */
    public boolean intersect(final Area o) {
        return (o.maxX >= minX &&
                o.maxY >= minY &&
                o.maxZ >= minZ &&
                maxX >= o.minX &&
                maxY >= o.minY &&
                maxZ >= o.minZ);
    }

    /**
     * @param o to test
     * @return true case other container is inside this container
     */
    public boolean inside(final Area o) {
        return (o.maxY <= maxY &&
                o.minY >= minY &&
                o.maxZ <= maxZ &&
                o.minZ >= minZ &&
                o.maxX <= maxX &&
                o.minX >= minX);
    }

    public boolean inside(final int x, final int y, final int z) {
        return (minX <= x &&
                x <= maxX &&
                minY <= y &&
                y <= maxY &&
                minZ <= z &&
                z <= maxZ);
    }

    public boolean insideChunk(final int x, final int z) {
        return (minX >> 4 <= x &&
                x <= maxX >> 4 &&
                minZ >> 4 <= z &&
                z <= maxZ >> 4);
    }

    public int getTotalChunks() {
        return ((maxX >> 4) - (minX >> 4) + 1) * ((maxZ >> 4) - (minZ >> 4) + 1);
    }

    public int getTotalArea() {
        return (maxX - minX) * (maxY - minY) * (maxZ - minZ);
    }
}
