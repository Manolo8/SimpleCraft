package com.github.manolo8.simplecraft.core.world.model.container;

import com.github.manolo8.simplecraft.core.world.container.Area;
import com.github.manolo8.simplecraft.core.data.model.named.NamedDTO;

public class ContainerDTO extends NamedDTO {

    public int worldId;
    public int maxX;
    public int minX;
    public int maxZ;
    public int minZ;
    public int maxY;
    public int minY;

    public void load(Area area) {
        this.maxX = area.maxX;
        this.minX = area.minX;
        this.maxZ = area.maxZ;
        this.minZ = area.minZ;
        this.maxY = area.maxY;
        this.minY = area.minY;
    }
}
