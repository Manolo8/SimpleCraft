package com.github.manolo8.simplecraft.core.commands.inventory;

import java.util.Collection;
import java.util.List;

public interface View {

    InventoryView getInventoryView();

    void setInventoryView(InventoryView view);

    int getPage();

    boolean nextPage();

    boolean previousPage();

    String getTitle();

    List<? extends ItemAction> getActions();

    List<? extends ItemAction> getPagination();

    /**
     * Chamada quando a view não é mais usada
     */
    void addReference();

    /**
     * Chamado quando a view é removida
     */
    void removeReference();
}
