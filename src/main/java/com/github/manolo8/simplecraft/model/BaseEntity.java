package com.github.manolo8.simplecraft.model;

import java.util.UUID;

/**
 * BaseEntity
 *
 * UUID = unique identification
 * references = how many objects reference this object
 * 1 = only the cache system and probably will
 * be removed soon
 */
public class BaseEntity {

    private Integer id;
    private Integer references;
    private long lastCheck;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public boolean isNew() {
        return id == null;
    }

    public Integer getReferences() {
        return references;
    }

    public void setReferences(Integer references) {
        this.references = references;
    }

    public void addReference() {
        this.references++;
    }

    public void removeReference() {
        this.references--;
    }

    public long getLastCheck() {
        return lastCheck;
    }

    public void setLastCheck(long lastCheck) {
        this.lastCheck = lastCheck;
    }

    public boolean match(Integer id) {
        return this.id.equals(id);
    }
}