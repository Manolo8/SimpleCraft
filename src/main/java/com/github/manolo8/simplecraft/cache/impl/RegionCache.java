package com.github.manolo8.simplecraft.cache.impl;

import com.github.manolo8.simplecraft.cache.NamedCache;
import com.github.manolo8.simplecraft.cache.SaveCache;
import com.github.manolo8.simplecraft.data.dao.RegionDao;
import com.github.manolo8.simplecraft.domain.region.Region;

public class RegionCache extends NamedCache<Region> implements SaveCache<Region> {

    private final RegionDao regionDao;

    public RegionCache(RegionDao regionDao) {
        super(Region.class);
        this.regionDao = regionDao;
    }

    @Override
    public void save(Region region) {
        regionDao.save(region);
    }
}
