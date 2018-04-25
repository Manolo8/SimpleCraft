package com.github.manolo8.simplecraft.modules.region.data;

import com.github.manolo8.simplecraft.cache.impl.RegionCache;
import com.github.manolo8.simplecraft.data.dao.RegionDao;
import com.github.manolo8.simplecraft.modules.region.Region;

import java.util.ArrayList;
import java.util.List;

public class RegionRepository {

    private final RegionDao regionDao;
    private final RegionCache regionCache;

    public RegionRepository(RegionCache regionCache,
                            RegionDao regionDao) {
        this.regionDao = regionDao;
        this.regionCache = regionCache;
    }

    public Region create(String name, int worldId) {
        if (findOne(name) != null) return null;

        RegionDTO dto = regionDao.create(name, worldId);

        return fromDTO(dto);
    }

    public Region findOne(int id) {
        Region region = regionCache.getIfMatch(id);

        if (region != null) return region;

        RegionDTO dto = regionDao.findOne(id);

        return dto == null ? null : fromDTO(dto);
    }

    public Region findOne(String name) {
        Region region = regionCache.getIfMatch(name);

        if (region != null) return region;

        RegionDTO dto = regionDao.findOne(name);

        return dto == null ? null : fromDTO(dto);
    }

    public List<Region> findAllByWorld(int worldId) {
        List<Region> list = new ArrayList<>();

        for (RegionDTO dto : regionDao.findAllByWorld(worldId)) {
            Region region = regionCache.getIfMatch(dto.getId());
            list.add(region == null ? fromDTO(dto) : region);
        }

        return list;
    }

    private Region fromDTO(RegionDTO dto) {
        Region region = new Region();

        region.setId(dto.getId());
        region.setName(dto.getName());
        region.setArea(dto.getArea());
        region.setCanBreak(dto.isCanBreak());
        region.setCanExplode(dto.isCanExplode());
        region.setCanInteract(dto.isCanInteract());
        region.setCanPlace(dto.isCanPlace());
        region.setCanPistonWork(dto.isCanPistonWork());
        region.setPvpOn(dto.isPvpOn());
        region.setCanSpread(dto.isCanSpread());
        region.setPvpAnimalOn(dto.isPvpAnimalOn());
        region.setWorldId(dto.getWorldId());
        region.setNeedSave(false);

        regionCache.add(region);

        return region;
    }
}
