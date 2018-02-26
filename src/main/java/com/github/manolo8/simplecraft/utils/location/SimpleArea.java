package com.github.manolo8.simplecraft.utils.location;

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
}
