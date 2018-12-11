package com.github.manolo8.simplecraft.module.portal;

import com.github.manolo8.simplecraft.core.world.container.Container;
import com.github.manolo8.simplecraft.interfaces.Enter;
import com.github.manolo8.simplecraft.module.user.User;
import com.github.manolo8.simplecraft.utils.location.SimpleLocation;
import org.bukkit.Location;

public class Portal extends Container implements Enter {

    private SimpleLocation location;

    private String message;

    //======================================================
    //=====================ENCAPSULATION====================
    //======================================================
    public SimpleLocation getLocation() {
        return location;
    }

    public void setLocation(SimpleLocation location) {
        this.location = location;
        modified();
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
        modified();
    }
    //======================================================
    //=====================ENCAPSULATION====================
    //======================================================

    //======================================================
    //========================METHODS=======================
    //======================================================

    @Override
    public void onEnter(User user) {
        if (location == null) {
            user.sendAction("§cPortal quebrado!");
        } else if (user.isInPvp()) {
            user.sendAction("§cVocê está em PVP! Espere " + (15 - user.getLastPvpTime()) + " segundos");
        } else {
            if (user.teleport(location.getOf(worldInfo.getWorld()).add(0.5, 0, 0.5)))
                user.sendTitle("§aPortais", message);
        }
    }

    @Override
    public boolean teleport(User user) {
        return user.teleport(new Location(worldInfo.getWorld(), area.maxX, area.maxY+ 10, area.maxZ));
    }

    //======================================================
    //=======================_METHODS=======================
    //======================================================
}
