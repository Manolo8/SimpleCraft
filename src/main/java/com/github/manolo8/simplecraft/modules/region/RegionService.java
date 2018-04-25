package com.github.manolo8.simplecraft.modules.region;

import com.github.manolo8.simplecraft.core.world.IWorld;
import com.github.manolo8.simplecraft.core.world.IWorldProducer;
import com.github.manolo8.simplecraft.core.world.WorldService;
import com.github.manolo8.simplecraft.modules.mob.MobService;
import com.github.manolo8.simplecraft.modules.region.data.RegionRepository;
import com.github.manolo8.simplecraft.modules.user.User;
import com.github.manolo8.simplecraft.utils.RegionUtils;
import com.github.manolo8.simplecraft.utils.location.SimpleArea;

import java.util.ArrayList;
import java.util.List;

public class RegionService implements IWorldProducer {

    private final WorldService worldService;
    private final RegionRepository regionRepository;
    private List<IWorldRegion> iWorlds;
    private MobService mobService;

    public RegionService(RegionRepository regionRepository,
                         WorldService worldService,
                         MobService mobService) {
        this.worldService = worldService;
        this.regionRepository = regionRepository;
        this.iWorlds = new ArrayList<>();
        this.mobService = mobService;

        worldService.addProducer(this);
    }

    public Region create(User user, String name) {
        int service = worldService.getWorldProtectionService(user.getWorldId());

        //Region = 1
        if (service != 1) return null;

        IWorldRegion iWorldRegion = findIWorldRegion(user.getWorldId());

        SimpleArea area = new SimpleArea(user.getPos1(), user.getPos2());

        if (!RegionUtils.isAvailable(iWorldRegion, area)) return null;

        Region region = regionRepository.create(name, user.getWorldId());

        if (region == null) return null;

        region.setArea(new SimpleArea(user.getPos1(), user.getPos2()));

        RegionUtils.addToIWorldRegion(iWorldRegion, region);

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

        regions = RegionUtils.order(regions);

        IWorldRegion iWorldRegion = new IWorldRegion(worldId);

        for (Region region : regions) {
            iWorldRegion.addRegion(region);
            mobService.regionLoad(region);
        }

        this.iWorlds.add(iWorldRegion);


        return iWorldRegion;
    }

    @Override
    public void unload(IWorld iWorld) {
        //Quando o mundo e descarregado
        //Remove as referencias do sistema
        //De cache
        for (Region region : ((IWorldRegion) iWorld).getRegions()) {
            region.removeReference();
            mobService.regionUnload(region);
        }

        iWorlds.remove(iWorld);
    }
}
