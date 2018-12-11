package com.github.manolo8.simplecraft.core.commands.inventory;

import java.sql.SQLException;
import java.util.List;

public interface View {

    InventoryView getMain();

    void setMain(InventoryView view);

    int size();

    String getTitle();

    Handler createHandler();

    void close();

    void tick();
}
