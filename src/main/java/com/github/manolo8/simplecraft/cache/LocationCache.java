package com.github.manolo8.simplecraft.cache;

import com.github.manolo8.simplecraft.data.model.LocationEntity;

public class LocationCache<T extends LocationEntity> extends Cache<T> {

    public LocationCache(Class clazz) {
        super(clazz);
    }

    public T getIfMatch(int x, int y, int z, int worldId) {
        for (T t : getCached())
            if (t.match(x, y, z, worldId)) return t;
        return null;
    }

}
