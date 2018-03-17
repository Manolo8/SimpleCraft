package com.github.manolo8.simplecraft.cache;

import com.github.manolo8.simplecraft.data.model.BaseEntity;
import com.github.manolo8.simplecraft.data.model.NamedEntity;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CacheManager implements Runnable {

    private List<Cache> cacheList;

    public CacheManager() {
        cacheList = new ArrayList<>();
    }

    public void addCache(Cache cache) {
        this.cacheList.add(cache);
    }

    public void saveAll() {
        for (Cache cache : cacheList)
            if (cache instanceof SaveCache)
                for (Object object : cache.getCached()) {
                    BaseEntity entity = (BaseEntity) object;

                    if(entity.isNeedSave())
                        ((SaveCache) cache).save(entity);
                }
    }

    @Override
    public void run() {
        for (Cache cache : cacheList) {
            for (Object object : cache.getCached()) {
                BaseEntity baseEntity = (BaseEntity) object;

                if (System.currentTimeMillis() - baseEntity.getLastCheck() < 60000) continue;

                if (baseEntity.getReferences() > 1) {
                    baseEntity.setLastCheck(System.currentTimeMillis());
                    continue;
                }

                if (cache instanceof SaveCache && baseEntity.isNeedSave())
                    ((SaveCache) cache).save(baseEntity);

                if(baseEntity instanceof NamedEntity) {
                    System.out.println("Removed " + ((NamedEntity) baseEntity).getName());
                } else {
                    System.out.println("Removed " + baseEntity.getClass().getSimpleName());
                }

                cache.remove(baseEntity);
                break;
            }
        }
    }
}
