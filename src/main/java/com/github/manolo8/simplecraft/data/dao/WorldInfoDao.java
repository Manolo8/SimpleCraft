package com.github.manolo8.simplecraft.data.dao;

import com.github.manolo8.simplecraft.core.world.WorldInfo;

import java.util.List;

public interface WorldInfoDao {

    List<WorldInfo> loadAll();

    void save(WorldInfo info);

    WorldInfo create(WorldInfo info);
}
