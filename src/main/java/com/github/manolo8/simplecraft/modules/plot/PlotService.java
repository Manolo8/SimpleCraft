package com.github.manolo8.simplecraft.modules.plot;

import com.github.manolo8.simplecraft.core.world.IWorld;
import com.github.manolo8.simplecraft.core.world.IWorldProducer;
import com.github.manolo8.simplecraft.core.world.WorldService;
import com.github.manolo8.simplecraft.modules.plot.data.PlotInfo;
import com.github.manolo8.simplecraft.modules.plot.data.PlotRepository;
import com.github.manolo8.simplecraft.modules.user.User;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PlotService implements IWorldProducer, Runnable {

    private final WorldService worldService;
    private final PlotRepository plotRepository;
    private final List<PlotInfo> attemptToLoad;
    private final List<IWorldPlot> iWorldPlots;

    public PlotService(WorldService worldService, PlotRepository plotRepository) {
        this.worldService = worldService;
        this.plotRepository = plotRepository;
        this.iWorldPlots = new ArrayList<>();
        this.attemptToLoad = new ArrayList<>();
        worldService.addProducer(this);
    }

    public Plot autoCreatePlot(User user) {

        int serviceId = worldService.getWorldProtectionService(user.getWorldId());
        int worldId;

        //Service 2 = Plot
        //Primeiro checa se o jogador esta em um mundo
        //Plot, se não estiver, procura um mundo plot
        if (serviceId == 2) worldId = user.getWorldId();
        else worldId = worldService.getWorldIdByService(2);

        //-1 = não tem mundo com esse serviço
        if (worldId == -1) return null;

        Plot plot = plotRepository.autoCreate(worldId);

        if (plot == null) return null;

        plot.setOwner(user.getId());
        user.getPlots().add(plot.getInfo());

        IWorldPlot iWorldPlot = (IWorldPlot) worldService.getIWorld(worldId);
        iWorldPlot.addPlot(plot);

        return plot;
    }

    public Plot createPlot(User user) {

        int serviceId = worldService.getWorldProtectionService(user.getWorldId());

        //Service 2 = plot ...
        if (serviceId != 2) return null;

        Location l = user.getBase().getLocation();
        int x = (int) l.getX() >> 6;
        int z = (int) l.getZ() >> 6;

        Plot plot = plotRepository.create(x, z, user.getWorldId());

        if (plot == null) return null;

        plot.setOwner(user.getId());
        user.getPlots().add(plot.getInfo());

        IWorldPlot iWorldPlot = (IWorldPlot) worldService.getIWorld(user.getWorldId());
        iWorldPlot.addPlot(plot);

        return plot;
    }

    public void checkIfContains(int x, int z, int worldId) {
        PlotInfo info = plotRepository.getIfOwned(x, z, worldId);
        if (info == null) return;
        if (attemptToLoad.contains(info)) return;
        attemptToLoad.add(info);
    }

    private void plotLoaded(Plot plot) {
        for (IWorldPlot iWorldPlot : iWorldPlots) {
            if (iWorldPlot.match(plot.getWorldId()))
                iWorldPlot.addPlot(plot);
        }
    }

    @Override
    public int getType() {
        return 2;
    }

    @Override
    public IWorld produce(int worldId) {
        IWorldPlot iWorldPlot = new IWorldPlot(this, worldId);

        this.iWorldPlots.add(iWorldPlot);

        return iWorldPlot;
    }

    @Override
    public void unload(IWorld iWorld) {

    }

    @Override
    public void run() {
        //Número máximo de plot que podem ser carregados
        //Do banco por segundo:
        int max = 3;

        Iterator<PlotInfo> i = attemptToLoad.iterator();

        while (i.hasNext() && max != 0) {
            max--;
            Plot plot = plotRepository.findOne(i.next());

            if (plot != null) plotLoaded(plot);

            System.out.println(plot.getId() + " loaded in");
            i.remove();
        }
    }
}
