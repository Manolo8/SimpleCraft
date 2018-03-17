package com.github.manolo8.simplecraft.cache.impl;

import com.github.manolo8.simplecraft.cache.Cache;
import com.github.manolo8.simplecraft.cache.SaveCache;
import com.github.manolo8.simplecraft.data.dao.PlotDao;
import com.github.manolo8.simplecraft.domain.plot.Plot;

public class PlotCache extends Cache<Plot> implements SaveCache<Plot>{

    private final PlotDao plotDao;

    public PlotCache(PlotDao plotDao) {
        super(Plot.class);
        this.plotDao = plotDao;
    }

    @Override
    public void save(Plot plot) {
        System.out.println("plot " + plot.getId() + " saved");
        plotDao.save(plot);
    }
}
