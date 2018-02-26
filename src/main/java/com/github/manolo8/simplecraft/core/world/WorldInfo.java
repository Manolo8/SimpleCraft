package com.github.manolo8.simplecraft.core.world;

import com.github.manolo8.simplecraft.model.BaseEntity;

import java.util.UUID;

public class WorldInfo extends BaseEntity{

    private UUID uuid;
    private int protectionService;

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public int getProtectionService() {
        return protectionService;
    }

    public void setProtectionService(int protectionService) {
        this.protectionService = protectionService;
    }
}
