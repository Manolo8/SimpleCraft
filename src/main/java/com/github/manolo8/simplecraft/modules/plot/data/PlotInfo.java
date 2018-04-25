package com.github.manolo8.simplecraft.modules.plot.data;

import com.github.manolo8.simplecraft.data.model.PositionEntity;

public class PlotInfo extends PositionEntity implements Comparable<PlotInfo> {

    private int ownerId;

    public PlotInfo(int id, int x, int z) {
        this.id = id;
        this.x = x;
        this.z = z;
    }

    public PlotInfo() {
    }


    public int getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(int ownerId) {
        this.ownerId = ownerId;
    }


    @Override
    public int compareTo(PlotInfo o) {
        return Integer.compare(getId(), o.getId());
    }
}
