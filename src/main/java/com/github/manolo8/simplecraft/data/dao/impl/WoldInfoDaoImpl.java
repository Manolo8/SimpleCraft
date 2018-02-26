package com.github.manolo8.simplecraft.data.dao.impl;

import com.github.manolo8.simplecraft.core.world.WorldInfo;
import com.github.manolo8.simplecraft.data.ConnectionBuilder;
import com.github.manolo8.simplecraft.data.dao.WorldInfoDao;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class WoldInfoDaoImpl implements WorldInfoDao {

    private final ConnectionBuilder builder;

    public WoldInfoDaoImpl(ConnectionBuilder builder) {
        this.builder = builder;
    }

    @Override
    public List<WorldInfo> loadAll() {
        return null;
    }

    @Override
    public void save(WorldInfo info) {

    }

    @Override
    public WorldInfo create(WorldInfo info) {
        return null;
    }

    private void defaults() {
        try {
            Statement statement = builder.getConnection().createStatement();

            statement.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS worlds" +
                            "(id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
                            "uuid VARCHAR(36)," +
                            "name  VARCHAR(32)," +
                            "protectionService INTEGER" +
                            ");"
            );

            statement.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
