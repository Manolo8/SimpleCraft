package com.github.manolo8.simplecraft.modules.plot.data;

import com.github.manolo8.simplecraft.cache.impl.PlotCache;
import com.github.manolo8.simplecraft.data.dao.PlotDao;
import com.github.manolo8.simplecraft.modules.plot.Plot;
import com.github.manolo8.simplecraft.utils.ChunkIDGenerator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PlotRepository {

    private final PlotDao plotDao;
    private final PlotCache plotCache;
    private final List<PlotInfo> owneds;

    public PlotRepository(PlotDao plotDao, PlotCache plotCache) {
        this.plotDao = plotDao;
        this.plotCache = plotCache;
        this.owneds = plotDao.findAllOwned();
    }

    public Plot autoCreate(int worldId) {
        return create(findNextAvailable(worldId));
    }

    public Plot create(int x, int z, int worldId) {
        int id = ChunkIDGenerator.generate(x, z);

        if (!isAvailable(id, worldId)) return null;

        PlotInfo info = new PlotInfo(id, x, z);
        info.setWorldId(worldId);

        return create(info);
    }

    public PlotInfo getIfOwned(int x, int z, int worldId) {
        for (PlotInfo info : owneds)
            if (info.getWorldId() == worldId
                    && info.getX() == x
                    && info.getZ() == z) return info;

        return null;
    }

    public List<PlotInfo> findUserPlots(int id) {
        List<PlotInfo> temp = new ArrayList<>();

        for (PlotInfo info : owneds)
            if (info.getOwnerId() == id)
                temp.add(info);

        return temp;
    }

    public Plot findOne(PlotInfo info) {
        Plot plot = plotCache.getIfMatch(info.getId());

        if (plot != null) return plot;

        PlotDTO dto = plotDao.findOne(info);

        if (dto == null) return null;

        return fromDTO(dto, info);
    }

    private Plot create(PlotInfo info) {
        PlotDTO dto = plotDao.create(info);

        if (dto == null) return null;

        owneds.add(info);

        return fromDTO(dto, info);
    }

    private PlotInfo findNextAvailable(int worldId) {
        PlotInfo info = ChunkIDGenerator.generate(nextAvailable(worldId));

        info.setWorldId(worldId);

        return info;
    }

    private boolean isAvailable(int id, int worldId) {
        for (PlotInfo info : owneds)
            if (info.getWorldId() == worldId
                    && info.getId() == id) return false;
        return true;
    }

    private int nextAvailable(int worldId) {
        //Organiza os plots por id
        Collections.sort(owneds);
        //Seta o último para o 0
        int last = 0;

        //0
        //1
        //3

        //Verifica se há alguma coluna faltando
        for (PlotInfo info : owneds) {
            //Se o mundo não for o mesmo, continua
            if (info.getWorldId() != worldId) continue;
            //Se for diferente é por que ta faltando,
            if (info.getId() != last) return last;
            last++;
        }

        //Se não tiver nem um faltando
        //Retorna o último +1 :)
        return last;
    }

    private Plot fromDTO(PlotDTO dto, PlotInfo info) {
        Plot plot = new Plot(info);

        plot.setId(dto.getId());
        plot.setX(dto.getX());
        plot.setZ(dto.getZ());
        plot.setWorldId(dto.getWorldId());
        plot.setSellPrice(dto.getSellPrice());
        plot.setPvpOn(dto.isPvpOn());
        plot.setPvpAnimalOn(dto.isPvpAnimalOn());
        plot.setOwner(dto.getOwner());
        plot.setFriends(dto.getFriends());
        plot.setNeedSave(false);

        plotCache.add(plot);

        return plot;
    }
}
