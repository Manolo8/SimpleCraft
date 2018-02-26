package com.github.manolo8.simplecraft.cache;

import com.github.manolo8.simplecraft.model.BaseEntity;

import java.util.ArrayList;
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
                for (Object object : cache.getCached())
                    ((SaveCache) cache).save(object);
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

                if (cache instanceof SaveCache)
                    ((SaveCache) cache).save(baseEntity);

                cache.remove(baseEntity);
                break;
            }
        }
    }
}
