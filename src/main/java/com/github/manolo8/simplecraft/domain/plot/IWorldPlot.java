package com.github.manolo8.simplecraft.domain.plot;

import com.github.manolo8.simplecraft.core.protection.Protection;
import com.github.manolo8.simplecraft.core.protection.ProtectionChecker;
import com.github.manolo8.simplecraft.core.protection.impl.DefaultProtection;
import com.github.manolo8.simplecraft.core.world.IWorld;
import org.bukkit.Chunk;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

public class IWorldPlot implements IWorld, ProtectionChecker {

    private int worldId;
    private List<Plot> plots;
    private Protection defaultProtection;
    private PlotService plotService;

    public IWorldPlot(PlotService plotService, int worldId) {
        this.worldId = worldId;
        this.plots = new ArrayList<>();
        this.defaultProtection = new DefaultProtection();
        this.plotService = plotService;
    }

    public void addPlot(Plot plot) {
        this.plots.add(plot);
        //Adiciona uma referencia ao plot
        //Para não ser removido do cache
        plot.addReference();
    }

    private boolean isStreet(int x, int z) {
        return (z % 4 == 0 || x % 4 == 0);
    }

    private Plot getIfLoaded(int x, int z) {
        for (Plot plot : plots) if (plot.match(x, z)) return plot;
        return null;
    }

    @Override
    public boolean match(int worldId) {
        return this.worldId == worldId;
    }

    @Override
    public void chunkLoad(int x, int z) {
        //x e y de uma chunk, temos que transformar para equivaler
        //A uma proteção
        x = x >> 2;
        z = z >> 2;


        //Checa se há alguma proteção na área
        //!!!(A informação esta na memória)!!!
        //Se tiver, será carregado do banco de dados
        //Por uma runnable, que carrega 3 plots por segundo
        //O jogador vai perceber que não tem permissão
        //No plot dele por um tempinho, mas não vai dar bola :)
        //Iremos usar o ChunkUnloadEvent para descarregar esses plots
        if (getIfLoaded(x, z) != null) return;

        plotService.checkIfContains(x, z, worldId);
    }

    @Override
    public void chunkUnload(int x, int z, Chunk[] chunks) {
        //x e y de uma chunk, temos que transformar para equivaler
        //A uma proteção
        x = x >> 2;
        z = z >> 2;

        Plot plot = getIfLoaded(x, z);

        if (plot == null) return;

        int count = 0;

        for (Chunk chunk : chunks) {
            int cx = chunk.getX() >> 2;
            int cz = chunk.getZ() >> 2;
            if (cx != x || cz != z) continue;
            count++;
        }

        //Se o count for = 1 significa que
        //A única chunk que mantinha a proteção
        //Carregada foi descarregada.
        if (count == 1) {
            //Removes do sistema
            this.plots.remove(plot);
            //E então removes uma referência
            //Assim em breve a proteção será salva
            //E descarregada pelo cache
            plot.removeReference();
        }
    }

    @Override
    public ProtectionChecker getChecker() {
        return this;
    }

    @Override
    public Protection getLocationProtection(Location location) {
        int x = (int) location.getX() >> 4;
        int z = (int) location.getZ() >> 4;

        //Se for rua, retorna a proteção padrão
        if (isStreet(x, z)) return defaultProtection;

        x = x >> 2;
        z = z >> 2;

        //Checa se o jogador está em algum plot com dono
        for (Plot plot : plots) if (plot.match(x, z)) return plot;

        return defaultProtection;
    }
}
