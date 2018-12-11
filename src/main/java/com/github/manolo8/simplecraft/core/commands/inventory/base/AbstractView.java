package com.github.manolo8.simplecraft.core.commands.inventory.base;

import com.github.manolo8.simplecraft.core.commands.inventory.Handler;
import com.github.manolo8.simplecraft.core.commands.inventory.InventoryView;
import com.github.manolo8.simplecraft.core.commands.inventory.View;
import com.github.manolo8.simplecraft.module.user.User;

public abstract class AbstractView implements View {

    protected InventoryView main;

    public InventoryView getMain() {
        return main;
    }

    public void setMain(InventoryView main) {
        this.main = main;
    }

    public User user() {
        return main.user();
    }

    @Override
    public void tick() {

    }
}
