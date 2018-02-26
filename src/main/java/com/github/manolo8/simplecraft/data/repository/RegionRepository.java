package com.github.manolo8.simplecraft.data.repository;

import com.github.manolo8.simplecraft.cache.impl.RegionCache;
import com.github.manolo8.simplecraft.data.dao.RegionDao;
import com.github.manolo8.simplecraft.data.dto.RegionDTO;
import com.github.manolo8.simplecraft.domain.region.Region;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class RegionRepository {

    private final RegionDao regionDao;
    private final RegionCache regionCache;

    public RegionRepository(RegionDao regionDao,
                            RegionCache regionCache) {
        this.regionDao = regionDao;
        this.regionCache = regionCache;
    }

    public Region create(String name, UUID world) {
        if (findOne(name) != null) return null;

        return fromDTO(regionDao.create(name, world));
    }

    public Region findOne(int id) {
        Region region = regionCache.getIfMatch(id);

        if (region != null) return region;

        return fromDTO(regionDao.findOne(id));
    }

    public Region findOne(String name) {
        Region region = regionCache.getIfMatch(name);

        if (region != null) return region;

        return fromDTO(regionDao.findOne(name));
    }

    public List<Region> findAllByWorld(UUID world) {
        List<Region> list = new ArrayList<>();

        for (RegionDTO dto : regionDao.findAllByWorld(world))
            list.add(fromDTO(dto));

        return list;
    }

    private Region fromDTO(RegionDTO dto) {
        if (dto == null) return null;

        //pequeno hacking para o findByWorld
        //Para retornar o mesmo objeto na memória
        Region region = regionCache.getIfMatch(dto.getId());

        if (region != null) return region;

        region = new Region();

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
        region.setWorld(Bukkit.getWorld(dto.getWorld()));

        regionCache.add(region);

        return region;
    }
}
