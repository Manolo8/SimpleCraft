package com.github.manolo8.simplecraft.modules.plot.data;

import com.github.manolo8.simplecraft.data.builder.ConnectionBuilder;
import com.github.manolo8.simplecraft.data.dao.PlotDao;
import com.github.manolo8.simplecraft.modules.plot.Plot;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class PlotDaoImpl implements PlotDao {

    private final ConnectionBuilder builder;
    private final String findAllOwnedQuery = "SELECT id,x,z,world_id,owner_id FROM plots";
    private final String insertQuery = "INSERT INTO plots (id,x,z,world_id) VALUES (?,?,?,?)";
    private final String addFriendQuery = "INSERT INTO plots_friends (plot_id, world_id, user_id) VALUES (?,?,?)";
    private final String updateQuery = "UPDATE plots SET owner_id=?,pvpAnimalOn=?,pvpOn=?,sellPrice=? WHERE id=? AND world_id=?";
    private final String findOneQuery = "SELECT p.*,pf.plot_id,pf.user_id FROM plots AS p LEFT JOIN plots_friends AS pf ON pf.plot_id=id AND pf.world_id=p.world_id WHERE id=? AND p.world_id=?";
    private final String findFriendsQuery = "SELECT user_id FROM plots_friends WHERE plot_id=? AND world_id=?";
    //O mundo é meio desnecessário, mas o impossível sempre pode ocorrer '-'
    private final String deleteFriendQuery = "DELETE FROM plots_friends WHERE plot_id=? AND world_id=?";

    public PlotDaoImpl(ConnectionBuilder builder) {
        this.builder = builder;

        defaults();
    }

    @Override
    public PlotDTO create(PlotInfo info) {
        try {
            PreparedStatement statement = builder.getConnection().prepareStatement(insertQuery);

            statement.setInt(1, info.getId());
            statement.setInt(2, info.getX());
            statement.setInt(3, info.getZ());
            statement.setInt(4, info.getWorldId());

            statement.executeUpdate();

            statement.close();

            return findOne(info);

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public PlotDTO findOne(PlotInfo info) {
        try {
            PreparedStatement statement = builder.getConnection().prepareStatement(findOneQuery);

            statement.setInt(1, info.getId());
            statement.setInt(2, info.getWorldId());

            ResultSet result = statement.executeQuery();

            if (!result.next()) return null;

            PlotDTO dto = fromResult(result);

            statement.close();

            return dto;

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void save(Plot plot) {
        try {
            PreparedStatement statement = builder.getConnection().prepareStatement(updateQuery);

            //owner_id=?,pvpAnimalOn=?,pvpOn=?,sellPrice=? WHERE id=? AND world_id=?

            statement.setInt(1, plot.getOwner());
            statement.setBoolean(2, plot.isPvpAnimalOn());
            statement.setBoolean(3, plot.isPvpOn());
            statement.setDouble(4, plot.getSellPrice());
            statement.setInt(5, plot.getId());
            statement.setInt(6, plot.getWorldId());

            statement.executeUpdate();

            statement.close();

            statement = builder.getConnection().prepareStatement(deleteFriendQuery);

            //DELETE FROM plots_friends WHERE plot_id=?  AND world_id=?
            statement.setInt(1, plot.getId());
            statement.setInt(2, plot.getWorldId());

            statement.executeUpdate();
            statement.close();

            statement = builder.getConnection().prepareStatement(addFriendQuery);
            //plot_id, world_id, user_id
            statement.setInt(1, plot.getId());
            statement.setInt(2, plot.getWorldId());

            for (Integer i : plot.getFriends()) {
                statement.setInt(3, i);
                statement.executeUpdate();
            }

            statement.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<PlotInfo> findAllOwned() {
        try {
            PreparedStatement statement = builder.getConnection().prepareStatement(findAllOwnedQuery);

            ResultSet result = statement.executeQuery();

            List<PlotInfo> dtoList = new ArrayList<>();

            while (result.next()) {
                PlotInfo info = new PlotInfo();

                info.setId(result.getInt("id"));
                info.setX(result.getInt("x"));
                info.setZ(result.getInt("z"));
                info.setWorldId(result.getInt("world_id"));
                info.setOwnerId(result.getInt("owner_id"));

                dtoList.add(info);
            }

            statement.close();

            return dtoList;

        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    private PlotDTO fromResult(ResultSet result) throws SQLException {
        PlotDTO plot = new PlotDTO();

        plot.setId(result.getInt("id"));
        plot.setX(result.getInt("x"));
        plot.setZ(result.getInt("z"));
        plot.setWorldId(result.getInt("world_id"));
        plot.setOwner(result.getInt("owner_id"));
        plot.setPvpOn(result.getBoolean("pvpOn"));
        plot.setPvpAnimalOn(result.getBoolean("pvpAnimalOn"));
        plot.setSellPrice(result.getDouble("sellPrice"));

        List<Integer> friends = new ArrayList<>();

        do {
            int userId = result.getInt("user_id");
            if (userId == 0) continue;
            friends.add(userId);
        } while (result.next());

        plot.setFriends(friends);

        return plot;
    }

    private void defaults() {
        try {
            Statement statement = builder.getConnection().createStatement();

            statement.executeUpdate("CREATE TABLE IF NOT EXISTS plots" +
                    "(id INTEGER(11) NOT NULL," +
                    "x INTEGER(11) NOT NULL," +
                    "z INTEGER(11) NOT NULL," +
                    "world_id INTEGER(11) NOT NULL," +
                    "owner_id INTEGER(11)," +
                    "pvpOn BIT(1)," +
                    "pvpAnimalOn BIT(1)," +
                    "sellPrice DECIMAL(12,2));");

            statement.executeUpdate("CREATE TABLE IF NOT EXISTS plots_friends" +
                    "(plot_id INTEGER(11) NOT NULL," +
                    "world_id INTEGER(11) NOT NULL," +
                    "user_id INTEGER(11) NOT NULL);");

            statement.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
