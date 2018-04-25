package com.github.manolo8.simplecraft.modules.mob;

import org.bukkit.Location;
import org.bukkit.entity.Entity;

public class MobInfo {

    private Mob mob;
    private int quantity;

    public MobInfo(Mob mob, int quantity) {
        this.mob = mob;
        this.quantity = quantity;
    }

    public int getQuantity() {
        return quantity;
    }

    public boolean match(Entity entity) {
        return mob.match(entity);
    }

    public void spawn(Location location) {
        mob.spawnMobWithDetails(location);
    }
}
