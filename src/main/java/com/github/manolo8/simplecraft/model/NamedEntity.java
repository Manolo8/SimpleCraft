package com.github.manolo8.simplecraft.model;

public class NamedEntity extends BaseEntity {

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean match(String name) {
        return this.name.equals(name);
    }
}
