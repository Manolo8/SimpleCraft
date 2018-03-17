package com.github.manolo8.simplecraft.data.model;

public class NamedEntity extends BaseEntity {

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (this.name != null && !this.name.equals(name)) setNeedSave(true);
        this.name = name;
    }

    public boolean match(String name) {
        return this.name.equals(name);
    }
}
