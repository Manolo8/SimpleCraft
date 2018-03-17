package com.github.manolo8.simplecraft.core.world;

import com.github.manolo8.simplecraft.core.protection.ProtectionChecker;
import org.bukkit.Chunk;
import org.bukkit.World;

public interface IWorld {

    //Para checar se provider é de
    //Determinado mundo
    boolean match(int worldId);

    //Usado para avisar o provider
    //Que uma chunk foi carregada
    void chunkLoad(int x, int z);

    //Usado para avisar o provider
    //Que uma chunk foi descarregada
    void chunkUnload(int x, int z, Chunk[] chunks);

    //Para pegar o checker do provider
    ProtectionChecker getChecker();
}