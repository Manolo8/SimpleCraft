package com.github.manolo8.simplecraft.domain.region;

import com.github.manolo8.simplecraft.data.repository.RegionRepository;

public class RegionService {

    private final RegionRepository regionRepository;

    public RegionService(RegionRepository regionRepository) {
        this.regionRepository = regionRepository;
    }


}
