package com.github.manolo8.simplecraft.core.world;

import com.github.manolo8.simplecraft.data.model.NamedEntity;
import org.bukkit.World;

import java.util.UUID;

public class WorldInfo extends NamedEntity {

    private UUID uuid;
    private int protectionService;

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public boolean match(World world) {
        return uuid.equals(world.getUID());
    }

    public int getProtectionService() {
        return protectionService;
    }

    public void setProtectionService(int protectionService) {
        this.protectionService = protectionService;
    }
}
