package com.github.manolo8.simplecraft.data.builder;

import org.bukkit.plugin.Plugin;

import java.sql.Connection;
import java.sql.SQLException;

public class ConnectionBuilder {

    private DataBase dataBase;

    public Connection getConnection() {
        return dataBase.getConnection();
    }

    public void build(Plugin plugin) {
        Sqlite sqlite = new Sqlite();
        sqlite.setDataFolder(plugin.getDataFolder());
        this.dataBase = sqlite;
        sqlite.getConnection();
    }

    public void closeConnection() {
        try {
            dataBase.getConnection().close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
