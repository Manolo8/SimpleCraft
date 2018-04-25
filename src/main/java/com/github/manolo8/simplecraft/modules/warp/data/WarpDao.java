package com.github.manolo8.simplecraft.modules.warp.data;

import com.github.manolo8.simplecraft.modules.warp.Warp;
import org.bukkit.Location;

import java.util.List;

public interface WarpDao {

    List<Warp> findAll();

    Warp create(String name, Location location, int worldID);

    void save(Warp warp);
}
