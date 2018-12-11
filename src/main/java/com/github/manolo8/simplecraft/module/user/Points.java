package com.github.manolo8.simplecraft.module.user;

import com.github.manolo8.simplecraft.utils.location.SimpleArea;
import com.github.manolo8.simplecraft.utils.location.SimpleLocation;
import org.bukkit.Location;

public class Points {

    private SimpleLocation one;
    private SimpleLocation two;

    public void setOne(Location location) {
        this.one = new SimpleLocation(location);
    }

    public void setTwo(Location location) {
        this.two = new SimpleLocation(location);
    }

    public boolean isMarked() {
        return one != null && two != null;
    }

    public SimpleArea asSimpleArea() {
        return new SimpleArea(one, two);
    }
}
