package com.github.manolo8.simplecraft.core.commands.inventory;

import java.sql.SQLException;

public interface Action {

    void click() throws SQLException;

    interface Info extends Action {

        default void click() {
        }

        void click(ClickInfo info);
    }

}
