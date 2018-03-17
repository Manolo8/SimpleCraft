package com.github.manolo8.simplecraft.domain.region.data;

import com.github.manolo8.simplecraft.data.builder.ConnectionBuilder;
import com.github.manolo8.simplecraft.data.dao.RegionDao;
import com.github.manolo8.simplecraft.domain.region.Region;
import org.bukkit.Bukkit;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class RegionDaoImpl implements RegionDao {

    private final ConnectionBuilder builder;
    private final String insertQuery = "INSERT INTO regions (name, world_id, maxX, maxY, maxZ, minX, minY, minZ, pvpOn, pvpAnimalOn, canSpread, canPistonWork, canExplode, canBreak, canPlace, canInteract) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
    private final String updateQuery = "UPDATE regions SET name=?, maxX=?, maxY=?, maxZ=?, minX=?, minY=?, minZ=?, pvpOn=?, pvpAnimalOn=?, canSpread=?, canPistonWork=?, canExplode=?, canBreak=?, canPlace=?, canInteract=? WHERE id=?";
    private final String findOneQuery = "SELECT * FROM regions WHERE id=?";
    private final String deleteQuery = "DELETE FROM regions WHERE id=?";
    private final String findOneByNameQuery = "SELECT * FROM regions WHERE name=?";
    private final String findAllByWorldQuery = "SELECT * FROM regions WHERE world_id=?";

    public RegionDaoImpl(ConnectionBuilder builder) {
        this.builder = builder;

        defaults();
    }

    @Override
    public RegionDTO findOne(Integer id) {
        try {
            PreparedStatement statement = builder.getConnection().prepareStatement(findOneQuery);

            statement.setInt(1, id);

            ResultSet result = statement.executeQuery();

            RegionDTO dto;

            if (!result.next()) dto = null;
            else dto = fromResult(result);

            statement.close();

            return dto;

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public RegionDTO findOne(String name) {
        try {
            PreparedStatement statement = builder.getConnection().prepareStatement(findOneByNameQuery);

            statement.setString(1, name);

            ResultSet result = statement.executeQuery();

            RegionDTO dto;

            if (!result.next()) dto = null;
            else dto = fromResult(result);

            statement.close();

            return dto;

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<RegionDTO> findAllByWorld(int worldId) {
        try {
            PreparedStatement statement = builder.getConnection().prepareStatement(findAllByWorldQuery);

            statement.setInt(1, worldId);

            ResultSet result = statement.executeQuery();

            List<RegionDTO> list = new ArrayList<>();

            while (result.next()) list.add(fromResult(result));

            statement.close();

            return list;

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public RegionDTO create(String name, int worldId) {
        try {
            PreparedStatement statement = builder.getConnection().prepareStatement(insertQuery);
            //name, world_id, maxX, maxY, maxZ, minX, minY, minZ,
            //pvpOn, pvpAnimalOn, canSpread, canPistonWork, canExplode, canBreak, canPlace, canInteract

            statement.setString(1, name);
            statement.setInt(2, worldId);
            statement.setInt(3, 0);
            statement.setInt(4, 0);
            statement.setInt(5, 0);
            statement.setInt(6, 0);
            statement.setInt(7, 0);
            statement.setInt(8, 0);
            statement.setBoolean(9, false);
            statement.setBoolean(10, false);
            statement.setBoolean(11, false);
            statement.setBoolean(12, false);
            statement.setBoolean(13, false);
            statement.setBoolean(14, false);
            statement.setBoolean(15, false);
            statement.setBoolean(16, false);

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
    public void delete(Integer id) {
        try {
            PreparedStatement statement = builder.getConnection().prepareStatement(deleteQuery);

            statement.setInt(1, id);

            statement.executeUpdate();

            statement.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void save(Region region) {
        try {
            PreparedStatement statement = builder.getConnection().prepareStatement(updateQuery);

            //name=?, maxX=?, maxY=?, maxZ=?, minX=?, minY=?, minZ=?, pvpOn=?, pvpAnimalOn=?,
            // canSpread=?, canPistonWork=?, canExplode=?, canBreak=?, canPlace=?, canInteract=? WHERE id=?

            statement.setString(1, region.getName());
            statement.setInt(2, region.getArea().getMax().getX());
            statement.setInt(3, region.getArea().getMax().getY());
            statement.setInt(4, region.getArea().getMax().getZ());
            statement.setInt(5, region.getArea().getMin().getX());
            statement.setInt(6, region.getArea().getMin().getY());
            statement.setInt(7, region.getArea().getMin().getZ());
            statement.setBoolean(8, region.isPvpOn());
            statement.setBoolean(9, region.isPvpAnimalOn());
            statement.setBoolean(10, region.isCanSpread());
            statement.setBoolean(11, region.isCanPistonWork());
            statement.setBoolean(12, region.isCanExplode());
            statement.setBoolean(13, region.isCanBreak());
            statement.setBoolean(14, region.isCanPlace());
            statement.setBoolean(15, region.isCanInteract());
            statement.setInt(16, region.getId());

            statement.executeUpdate();

            statement.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private RegionDTO fromResult(ResultSet result) throws SQLException {
        RegionDTO dto = new RegionDTO();

        dto.setId(result.getInt("id"));
        dto.setName(result.getString("name"));
        dto.setWorldId(result.getInt("world_id"));
        dto.setMaxX(result.getInt("maxX"));
        dto.setMaxY(result.getInt("maxY"));
        dto.setMaxZ(result.getInt("maxZ"));
        dto.setMinX(result.getInt("minX"));
        dto.setMinY(result.getInt("minY"));
        dto.setMinZ(result.getInt("minZ"));
        dto.setPvpOn(result.getBoolean("pvpOn"));
        dto.setPvpAnimalOn(result.getBoolean("pvpAnimalOn"));
        dto.setCanSpread(result.getBoolean("canSpread"));
        dto.setCanPistonWork(result.getBoolean("canPistonWork"));
        dto.setCanExplode(result.getBoolean("canExplode"));
        dto.setCanBreak(result.getBoolean("canBreak"));
        dto.setCanPlace(result.getBoolean("canPlace"));
        dto.setCanInteract(result.getBoolean("canInteract"));

        return dto;
    }

    private void defaults() {
        try {
            Statement statement = builder.getConnection().createStatement();

            statement.executeUpdate("CREATE TABLE IF NOT EXISTS regions" +
                    "(id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                    "name VARCHAR(32)," +
                    "world_id INTEGER," +
                    "maxX INTEGER ," +
                    "maxY INTEGER ," +
                    "maxZ INTEGER ," +
                    "minX INTEGER ," +
                    "minY INTEGER ," +
                    "minZ INTEGER ," +
                    "pvpOn BIT(1) ," +
                    "pvpAnimalOn BIT(1) ," +
                    "canSpread BIT(1) ," +
                    "canPistonWork BIT(1) ," +
                    "canExplode BIT(1) ," +
                    "canBreak BIT(1) ," +
                    "canPlace BIT(1) ," +
                    "canInteract BIT(1) );");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
