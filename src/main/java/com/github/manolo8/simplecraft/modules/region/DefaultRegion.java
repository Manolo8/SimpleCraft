package com.github.manolo8.simplecraft.modules.region;

public class DefaultRegion extends Region {

    @Override
    public String getName() {
        return "GLOBAL";
    }

    @Override
    public boolean isGlobal() {
        return true;
    }
}
