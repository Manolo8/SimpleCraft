package com.github.manolo8.simplecraft.data.dao;

import com.github.manolo8.simplecraft.domain.plot.Plot;
import com.github.manolo8.simplecraft.domain.plot.data.PlotDTO;
import com.github.manolo8.simplecraft.domain.plot.data.PlotInfo;

import java.util.List;

public interface PlotDao {

    PlotDTO create(PlotInfo info);

    PlotDTO findOne(PlotInfo info);

    void save(Plot plot);
    List<PlotInfo> findAllOwned();
}
