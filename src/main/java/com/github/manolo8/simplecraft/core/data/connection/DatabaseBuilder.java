package com.github.manolo8.simplecraft.core.data.connection;

import org.bukkit.plugin.Plugin;

import java.sql.SQLException;

public class DatabaseBuilder {

    private Database database;

    public Database getDatabase() {
        return database;
    }

    public DatabaseBuilder build(Plugin plugin) {

        SQLite sqlite = new SQLite();
        sqlite.setDataFolder(plugin.getDataFolder());
        this.database = sqlite;
        sqlite.getConnection();


//        MySQL mySQL = new MySQL();
//
//        mySQL.setHost("localhost");
//        mySQL.setUsername("simplecraft");
//
//        mySQL.setPassword("*****");
//        mySQL.setDataBase("simplecraft");

//        this.database = mySQL;

        return this;
    }

    public void closeConnection() {
        try {
            database.getConnection().close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
