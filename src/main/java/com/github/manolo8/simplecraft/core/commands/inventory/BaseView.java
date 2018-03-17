package com.github.manolo8.simplecraft.core.commands.inventory;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseView implements View {

    private int page;
    private InventoryView inventoryView;

    protected BaseView() {
        page = 1;
    }

    public InventoryView getInventoryView() {
        return inventoryView;
    }

    public void setInventoryView(InventoryView inventoryView) {
        this.inventoryView = inventoryView;
    }

    public boolean nextPage() {
        page++;
        return true;
    }

    public int getPage() {
        return page;
    }

    public boolean previousPage() {
        if (page == 1) return false;
        page--;
        return true;
    }

    public List<? extends ItemAction> getPagination() {
        return new ArrayList<>();
    }

    @Override
    public void addReference() {
    }

    @Override
    public void removeReference() {
    }
}
