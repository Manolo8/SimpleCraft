package com.github.manolo8.simplecraft.utils.location;

import org.bukkit.Chunk;
import org.bukkit.Location;

public class SimpleArea {

    private SimpleLocation max;
    private SimpleLocation min;

    public SimpleArea(SimpleLocation one, SimpleLocation two) {
        this.max = SimpleLocation.mathMax(one, two);
        this.min = SimpleLocation.mathMin(one, two);
    }

    public SimpleLocation getMax() {
        return max;
    }

    public SimpleLocation getMin() {
        return min;
    }

    public boolean isInArea(Location location) {
        return location.getX() >= min.getX() && location.getX() <= max.getX()
                && location.getY() >= min.getY() && location.getY() <= max.getY()
                && location.getZ() >= min.getZ() && location.getZ() <= max.getZ();
    }

    public boolean isInside(SimpleArea area) {
        return isInArea(area.getMax()) && isInArea(area.getMin());
    }

    public boolean isConflicting(SimpleArea area) {
        return (isInArea(area.getMax()) || isInArea(area.getMin()));
    }

    private boolean isInArea(SimpleLocation location) {
        return location.getX() >= min.getX() && location.getX() <= max.getX()
                && location.getY() >= min.getY() && location.getY() <= max.getY()
                && location.getZ() >= min.getZ() && location.getZ() <= max.getZ();
    }

    public boolean isInside(Chunk chunk) {
        int minX = getMin().getX() >> 4;
        int minZ = getMin().getZ() >> 4;
        int maxX = getMax().getX() >> 4;
        int maxZ = getMax().getZ() >> 4;
        int x = chunk.getX();
        int z = chunk.getZ();
        return x >= minX && x <= maxX && z >= minZ && z <= maxZ;
    }
}
