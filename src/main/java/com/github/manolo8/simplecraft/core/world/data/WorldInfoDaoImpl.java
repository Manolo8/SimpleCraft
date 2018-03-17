package com.github.manolo8.simplecraft.core.world.data;

import com.github.manolo8.simplecraft.core.world.WorldInfo;
import com.github.manolo8.simplecraft.data.builder.ConnectionBuilder;
import com.github.manolo8.simplecraft.data.dao.WorldInfoDao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class WorldInfoDaoImpl implements WorldInfoDao {

    private final ConnectionBuilder builder;
    private final String insertQuery = "INSERT INTO worlds (uuid, name, protectionService) VALUES (?,?,?)";
    private final String updateQuery = "UPDATE worlds SET name=?,protectionService=? WHERE uuid=?";

    public WorldInfoDaoImpl(ConnectionBuilder builder) {
        this.builder = builder;
        defaults();
    }

    @Override
    public List<WorldInfo> loadAll() {
        try {
            Statement statement = builder.getConnection().createStatement();

            ResultSet result = statement.executeQuery("SELECT * FROM worlds");

            List<WorldInfo> list = new ArrayList<>();

            while (result.next()) list.add(fromResult(result));

            statement.close();

            return list;

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void save(WorldInfo info) {
        try {
            PreparedStatement statement = builder.getConnection().prepareStatement(updateQuery);

            statement.setString(1, info.getName());
            statement.setInt(2, info.getProtectionService());
            statement.setString(3, info.getUuid().toString());

            statement.executeUpdate();

            statement.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public WorldInfo create(String name, UUID world, int protectionService) {
        try {
            PreparedStatement statement = builder.getConnection().prepareStatement(insertQuery);

            statement.setString(1, world.toString());
            statement.setString(2, name);
            statement.setInt(3, protectionService);

            statement.executeUpdate();

            WorldInfo info = new WorldInfo();
            info.setId(statement.getGeneratedKeys().getInt(1));
            info.setName(name);
            info.setUuid(world);
            info.setProtectionService(protectionService);

            statement.close();

            return info;

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    private WorldInfo fromResult(ResultSet result) throws SQLException {
        WorldInfo info = new WorldInfo();

        info.setId(result.getInt("id"));
        info.setUuid(UUID.fromString(result.getString("uuid")));
        info.setName(result.getString("name"));
        info.setProtectionService(result.getInt("protectionService"));

        return info;
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
