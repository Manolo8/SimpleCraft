package com.github.manolo8.simplecraft.core.world;

import com.github.manolo8.simplecraft.core.protection.ProtectionChecker;
import com.github.manolo8.simplecraft.core.protection.impl.DefaultChecker;
import com.github.manolo8.simplecraft.core.protection.impl.DefaultIWorldProducer;
import com.github.manolo8.simplecraft.data.dao.WorldInfoDao;
import com.github.manolo8.simplecraft.modules.plot.generator.PlotGenerator;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.WorldCreator;

import java.util.ArrayList;
import java.util.List;

public class WorldService {

    public static WorldService instance;
    private WorldInfoDao worldInfoDao;
    private List<IWorldProducer> iWorldProducers;
    private List<IWorld> iWorldList;
    private ProtectionChecker defaultChecker;
    private List<WorldInfo> worldInfos;

    public WorldService(WorldInfoDao worldInfoDao) {
        this.worldInfoDao = worldInfoDao;
        this.iWorldList = new ArrayList<>();
        this.defaultChecker = new DefaultChecker();
        this.iWorldProducers = new ArrayList<>();
        this.worldInfos = worldInfoDao.loadAll();

        this.iWorldProducers.add(new DefaultIWorldProducer());

        instance = this;
    }

    public void init() {
        for (World world : Bukkit.getWorlds())
            worldLoad(world);

        //PlotWorld
        World plot = Bukkit.createWorld(new PlotGenerator("plot"));
        World test = Bukkit.createWorld(new WorldCreator("test"));
        worldLoad(plot);
        worldLoad(test);
    }

    public void addProducer(IWorldProducer producer) {
        this.iWorldProducers.add(producer);
    }

    public IWorld getIWorld(World world) {
        int worldId = getWorldId(world);

        for (IWorld iWorld : iWorldList) if (iWorld.match(worldId)) return iWorld;

        return null;
    }

    public IWorld getIWorld(int worldId) {
        for (IWorld iWorld : iWorldList) if (iWorld.match(worldId)) return iWorld;

        return null;
    }

    public int getWorldIdByService(int serviceId) {

        for (WorldInfo worldInfo : worldInfos) {
            if (worldInfo.getProtectionService() == serviceId) return worldInfo.getId();
        }

        return -1;
    }

    public int getWorldProtectionService(int worldId) {
        for (WorldInfo worldInfo : worldInfos) {
            if (worldInfo.match(worldId)) return worldInfo.getProtectionService();
        }

        //Isso não é pra ocorrer
        throw new RuntimeException();
    }

    public World getWorldByWorldId(int worldId) {
        for (WorldInfo worldInfo : worldInfos)
            if (worldInfo.getId().equals(worldId))
                return Bukkit.getWorld(worldInfo.getUuid());

        return null;
    }

    public void worldLoad(World world) {
        WorldInfo info = getOrCreateWorldInfo(world);
        if (hasWorld(info.getId())) return;

        IWorld iWorld = getProducer(info.getProtectionService()).produce(info.getId());
        iWorldList.add(iWorld);

        for (Chunk chunk : world.getLoadedChunks())
            iWorld.chunkLoad(chunk.getX(), chunk.getZ());
    }

    private IWorldProducer getProducer(int type) {
        for (IWorldProducer producer : iWorldProducers) {
            if (producer.getType() == type) return producer;
        }

        //Isso não é pra ocorrer
        throw new RuntimeException();
    }

    private WorldInfo getOrCreateWorldInfo(World world) {
        for (WorldInfo worldInfo : worldInfos)
            if (worldInfo.match(world)) return worldInfo;

        WorldInfo info = worldInfoDao.create(world.getName(), world.getUID(), 0);
        worldInfos.add(info);
        return info;
    }

    private boolean hasWorld(int worldId) {
        for (IWorld iWorld : iWorldList)
            if (iWorld.match(worldId)) return true;
        return false;
    }

    /**
     * @param world o mundo
     * @return um id para o mundo
     */
    public int getWorldId(World world) {
        for (WorldInfo info : worldInfos)
            if (info.match(world)) return info.getId();
        return -1;
    }

    public ProtectionChecker getChecker(World world) {
        return getChecker(getWorldId(world));
    }

    /**
     * @param worldId worldId
     * @return um determinado ProtectionCheker para o mundo
     * Caso não encontrar, vai retonar um padrão
     */
    public ProtectionChecker getChecker(int worldId) {

        for (IWorld iWorld : iWorldList)
            if (iWorld.match(worldId)) return iWorld.getChecker();

        Bukkit.getLogger().info("Usando sistema padrao de protecao para o mundo " + worldId);

        return defaultChecker;
    }
}
