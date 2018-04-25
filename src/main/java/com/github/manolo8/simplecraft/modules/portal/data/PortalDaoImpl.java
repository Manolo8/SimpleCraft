package com.github.manolo8.simplecraft.modules.portal.data;

import com.github.manolo8.simplecraft.data.builder.ConnectionBuilder;
import com.github.manolo8.simplecraft.modules.portal.Portal;
import com.github.manolo8.simplecraft.modules.user.User;
import com.github.manolo8.simplecraft.utils.location.SimpleLocation;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class PortalDaoImpl implements PortalDao {

    private final ConnectionBuilder builder;
    private final String findAllQuery = "SELECT * FROM portals";
    private final String findOneQuery = "SELECT * FROM portals WHERE id=?";
    private final String insertQuery = "INSERT INTO portals (name, world_id, pos1, pos2, pos1_message, pos2_message) VALUES (?,?,?,?,?,?)";
    private final String updateQuery = "UPDATE portals SET pos1=?,pos2=?,pos1_message=?,pos2_message=? WHERE id=?";

    public PortalDaoImpl(ConnectionBuilder builder) {
        this.builder = builder;

        defaults();
    }

    @Override
    public List<Portal> findAll() {
        List<Portal> portals = new ArrayList<>();

        try {
            PreparedStatement statement = builder.getConnection().prepareStatement(findAllQuery);

            ResultSet result = statement.executeQuery();

            while (result.next()) portals.add(fromResult(result));

            statement.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return portals;
    }

    @Override
    public Portal create(User user, String name) {
        try {
            PreparedStatement statement = builder.getConnection().prepareStatement(insertQuery);

            statement.setString(1, name);
            statement.setInt(2, user.getWorldId());
            statement.setString(3, user.getPos1().toString());
            statement.setString(4, user.getPos2().toString());
            statement.setString(5, "POS1");
            statement.setString(6, "POS2");

            statement.executeUpdate();

            int id = statement.getGeneratedKeys().getInt(1);

            statement.close();

            return findOne(id);

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void save(Portal portal) {
        try {
            PreparedStatement statement = builder.getConnection().prepareStatement(updateQuery);

            statement.setString(1, portal.getPos1().toString());
            statement.setString(2, portal.getPos2().toString());
            statement.setString(3, portal.getPos1Message());
            statement.setString(4, portal.getPos2Message());
            statement.setInt(5, portal.getId());

            statement.executeUpdate();

            int id = statement.getGeneratedKeys().getInt(1);

            statement.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Portal findOne(int id) {
        try {
            PreparedStatement statement = builder.getConnection().prepareStatement(findOneQuery);

            statement.setInt(1, id);

            ResultSet result = statement.executeQuery();

            Portal portal = null;

            if (result.next()) portal = fromResult(result);

            statement.close();

            return portal;

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }


    private Portal fromResult(ResultSet result) throws SQLException {
        Portal portal = new Portal();

        portal.setName(result.getString("name"));
        portal.setPos1(SimpleLocation.fromString(result.getString("pos1")));
        portal.setPos2(SimpleLocation.fromString(result.getString("pos2")));
        portal.setPos1Message(result.getString("pos1_message"));
        portal.setPos2Message(result.getString("pos2_message"));
        portal.setWorldId(result.getInt("world_id"));
        portal.setNeedSave(false);

        return portal;
    }

    private void defaults() {
        try {
            Statement statement = builder.getConnection().createStatement();

            statement.executeUpdate("CREATE TABLE IF NOT EXISTS portals" +
                    "(" +
                    "    id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "    name varchar," +
                    "    world_id INTEGER," +
                    "    pos1 varchar," +
                    "    pos2 varchar," +
                    "    pos1_message varchar," +
                    "    pos2_message varchar" +
                    ");");

            statement.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
