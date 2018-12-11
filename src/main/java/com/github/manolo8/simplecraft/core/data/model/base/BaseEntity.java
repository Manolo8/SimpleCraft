package com.github.manolo8.simplecraft.core.data.model.base;

import com.github.manolo8.simplecraft.core.data.cache.Cache;

public class BaseEntity {

    protected Integer id;
    private boolean removed;

    //CACHE
    private Cache cache;
    private long lastModified;
    private boolean modified;
    //CACHE

    //======================================================
    //====================ENCAPSULATION=====================
    //======================================================
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    //======================================================
    //===================_ENCAPSULATION=====================
    //======================================================


    //======================================================
    //========================CACHE=========================
    //======================================================

    /**
     * @return true if this entity is already removed
     */
    public boolean isRemoved() {
        return removed;
    }

    public boolean isModified() {
        return modified;
    }

    public Cache cache() {
        return cache;
    }

    public void cache(Cache cache) {
        this.cache = cache;
    }

    public long getLastModified() {
        return lastModified;
    }

    public void setLastModified(long lastModified) {
        this.lastModified = lastModified;
    }

    public void saved() {
        modified = false;
    }

    public void saving() {
        modified = true;
    }

    public void remove() {
        this.removed = true;
        modified();
    }

    public void modified() {
        if (cache != null) {
            cache.modified(this);
        }

        this.lastModified = System.currentTimeMillis();
    }

    //======================================================
    //=======================_CACHE=========================
    //======================================================

    @Override
    public int hashCode() {
        //USE ID HAS HASHCODE
        return id == null ? 0 : id;
    }

    @Override
    public boolean equals(Object obj) {
        //IDENTITY
        return this == obj;
    }
}