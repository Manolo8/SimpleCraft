package com.github.manolo8.simplecraft.module.mobarea.mobs;

import org.bukkit.Location;
import org.bukkit.entity.Creature;
import org.bukkit.entity.LivingEntity;

public class Mob {

    protected int id;
    protected String name;
    protected Class<? extends LivingEntity> clazz;

    public Mob(int id, String name, Class<? extends LivingEntity> clazz) {
        this.id = id;
        this.name = name;
        this.clazz = clazz;
    }

    public String getName() {
        return name;
    }

    public LivingEntity create(Location location) {
        return location.getWorld().spawn(location, clazz);
    }
}
