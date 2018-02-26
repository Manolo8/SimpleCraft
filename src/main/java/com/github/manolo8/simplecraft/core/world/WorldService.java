package com.github.manolo8.simplecraft.core.world;

import com.github.manolo8.simplecraft.core.protection.ProtectionChecker;
import com.github.manolo8.simplecraft.core.protection.impl.DefaultChecker;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.List;

public class WorldService {

    private List<IWorld> iWorldList;
    private ProtectionChecker defaultChecker;

    public WorldService() {
        this.iWorldList = new ArrayList<>();
        this.defaultChecker = new DefaultChecker();


    }

    /**
     * @param world world
     * @return um determinado ProtectionCheker para o mundo
     * Caso não encontrar, vai retonar um padrão
     */
    public ProtectionChecker getChecker(World world) {
        for (IWorld iWorld : iWorldList)
            if (iWorld.match(world)) return iWorld.getChecker();

        Bukkit.getLogger().info("Usando sistema padrao de protecao para o mundo " + world.getName());

        return defaultChecker;
    }
}
