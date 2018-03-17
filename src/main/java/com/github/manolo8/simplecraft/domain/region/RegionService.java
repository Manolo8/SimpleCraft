package com.github.manolo8.simplecraft.domain.region;

import com.github.manolo8.simplecraft.core.world.IWorld;
import com.github.manolo8.simplecraft.core.world.IWorldProducer;
import com.github.manolo8.simplecraft.core.world.WorldService;
import com.github.manolo8.simplecraft.domain.region.data.RegionRepository;
import com.github.manolo8.simplecraft.domain.user.User;

import java.util.ArrayList;
import java.util.List;

public class RegionService implements IWorldProducer {

    private final WorldService worldService;
    private final RegionRepository regionRepository;
    private List<IWorldRegion> iWorlds;

    public RegionService(RegionRepository regionRepository,
                         WorldService worldService) {
        this.worldService = worldService;
        this.regionRepository = regionRepository;
        this.iWorlds = new ArrayList<>();

        worldService.addProducer(this);
    }

    public Region create(User user, String name) {
        int service = worldService.getWorldProtectionService(user.getWorldId());

        //Region = 1
        if (service != 1) return null;

        Region region = regionRepository.create(name, user.getWorldId());

        findIWorldRegion(user.getWorldId()).addRegion(region);

        return region;
    }

    private IWorldRegion findIWorldRegion(int worldId) {
        for (IWorldRegion iWorldRegion : iWorlds) {
            if (iWorldRegion.match(worldId)) return iWorldRegion;
        }

        //Isso não deve ocorrer pois, já verificamos
        throw new RuntimeException();
    }

    public Region findOne(String name) {
        return regionRepository.findOne(name);
    }

    @Override
    public int getType() {
        return 1;
    }

    @Override
    public IWorld produce(int worldId) {
        List<Region> regions = regionRepository.findAllByWorld(worldId);
        IWorldRegion iWorldRegion = new IWorldRegion(worldId);

        for (Region region : regions) iWorldRegion.addRegion(region);

        this.iWorlds.add(iWorldRegion);

        return iWorldRegion;
    }

    @Override
    public void unload(IWorld iWorld) {
        //Quando o mundo e descarregado
        //Remove as referencias do sistema
        //De cache
        for (Region region : ((IWorldRegion) iWorld).getRegions())
            region.removeReference();
    }
}
