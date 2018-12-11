package com.github.manolo8.simplecraft.utils.location;

import com.github.manolo8.simplecraft.core.world.container.Area;

public class SimpleArea {

    private SimpleLocation max;
    private SimpleLocation min;

    public SimpleArea(SimpleLocation one, SimpleLocation two) {
        this.max = SimpleLocation.mathMax(one, two);
        this.min = SimpleLocation.mathMin(one, two);
    }

    public SimpleArea(int x, int y, int z) {
        this.max = new SimpleLocation(x, y, z);
        this.min = new SimpleLocation(x, y, z);
    }

    public SimpleArea(SimpleLocation base, int space) {
        this.max = new SimpleLocation(base.x + space, base.y + space, base.z + space);
        this.min = new SimpleLocation(base.x - space, base.y - space, base.z - space);
    }

    public SimpleLocation getMax() {
        return max;
    }

    public SimpleLocation getMin() {
        return min;
    }

    public Area build() {
        return new Area(this);
    }
}
