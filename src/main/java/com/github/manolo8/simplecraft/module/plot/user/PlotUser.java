package com.github.manolo8.simplecraft.module.plot.user;

import com.github.manolo8.simplecraft.module.user.model.identity.BaseIdentity;
import com.github.manolo8.simplecraft.module.plot.Plot;
import com.github.manolo8.simplecraft.utils.entity.LazyLoaderList;

import java.util.List;

public class PlotUser extends BaseIdentity {

    private LazyLoaderList<Plot> plots;

    //======================================================
    //===================ENCAPSULATION======================
    //======================================================
    public List<Plot> getPlots() {
        return plots.get();
    }

    public void setPlots(LazyLoaderList<Plot> plots) {
        this.plots = plots;
    }
    //======================================================
    //==================_ENCAPSULATION======================
    //======================================================


    //======================================================
    //=======================METHODS========================
    //======================================================
    public void add(Plot plot) {
        this.plots.add(plot);
    }

    public void remove(Plot plot) {
        this.plots.remove(plot);
    }

    public int getQuantity() {
        return plots.get().size();
    }
    //======================================================
    //======================_METHODS========================
    //======================================================
}
