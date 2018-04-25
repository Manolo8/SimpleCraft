package com.github.manolo8.simplecraft.modules.warp.data;

import com.github.manolo8.simplecraft.data.builder.ConnectionBuilder;
import com.github.manolo8.simplecraft.modules.warp.Warp;
import com.github.manolo8.simplecraft.utils.ItemStackUtils;
import com.github.manolo8.simplecraft.utils.location.SimpleLocation;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class WarpDaoImpl implements WarpDao {

    private final ConnectionBuilder builder;
    private final String findAllQuery = "SELECT * FROM warps";
    private final String insertQuery = "INSERT INTO warps (name, displayName, lore, worldId, x, y, z, yaw, pitch) VALUES (?,?,?,?,?,?,?,?,?)";
    private final String updateQuery = "UPDATE warps SET name=?,material=?,displayName=?,lore=?,x=?,y=?,z=?,yaw=?,pitch=?,ind=? WHERE id=?";

    public WarpDaoImpl(ConnectionBuilder builder) {
        this.builder = builder;

        defaults();
    }

    @Override
    public List<Warp> findAll() {
        try {
            PreparedStatement statement = builder.getConnection().prepareStatement(findAllQuery);

            ResultSet result = statement.executeQuery();

            List<Warp> list = new ArrayList<>();

            while (result.next()) list.add(fromResult(result));

            statement.close();

            return list;

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Warp create(String name, Location location, int worldId) {
        try {
            PreparedStatement statement = builder.getConnection().prepareStatement(insertQuery);
            //name, displayName, lore, worldId, x, y, z, yaw, pitch
            statement.setString(1, name);
            statement.setString(2, name);
            statement.setString(3, "");
            statement.setInt(4, worldId);
            statement.setInt(5, location.getBlockX());
            statement.setInt(6, location.getBlockY());
            statement.setInt(7, location.getBlockZ());
            statement.setFloat(8, location.getYaw());
            statement.setFloat(9, location.getPitch());

            statement.executeUpdate();

            statement.close();

            Warp warp = new Warp();
            warp.setId(statement.getGeneratedKeys().getInt(1));
            warp.setName(name);
            warp.setWorldId(worldId);
            warp.setLocation(new SimpleLocation(location));
            warp.setItemStack(new ItemStack(Material.STONE));
            warp.setIndex(-1);

            return warp;

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void save(Warp warp) {
        try {
            PreparedStatement statement = builder.getConnection().prepareStatement(updateQuery);
            //name=?,material=?,displayName=?,lore=?,x=?,y=?,z=?,yaw=?,pitch=?,ind=? WHERE id=?
            statement.setString(1, warp.getName());

            ItemMeta meta = warp.getItemStack().getItemMeta();
            String displayName = meta.getDisplayName();
            String lore = ItemStackUtils.loreToString(meta.getLore());
            statement.setInt(2, warp.getItemStack().getTypeId());
            statement.setString(3, displayName);
            statement.setString(4, lore);

            SimpleLocation loc = warp.getLocation();
            statement.setInt(5, loc.getX());
            statement.setInt(6, loc.getY());
            statement.setInt(7, loc.getZ());
            statement.setFloat(8, loc.getYaw());
            statement.setFloat(9, loc.getPitch());
            statement.setFloat(10, warp.getIndex());
            statement.setInt(11, warp.getId());

            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Warp fromResult(ResultSet result) throws SQLException {
        Warp warp = new Warp();

        warp.setId(result.getInt("id"));
        warp.setName(result.getString("name"));
        warp.setIndex(result.getInt("ind"));
        warp.setWorldId(result.getInt("worldId"));

        int material = result.getInt("material");
        String displayName = result.getString("displayName");
        String lore = result.getString("lore");

        warp.setItemStack(ItemStackUtils.create(material, displayName, lore));

        int x = result.getInt("x");
        int y = result.getInt("y");
        int z = result.getInt("z");
        float yaw = result.getFloat("yaw");
        float pitch = result.getFloat("pitch");

        warp.setLocation(new SimpleLocation(x, y, z, yaw, pitch));

        return warp;
    }

    private void defaults() {
        try {
            Statement statement = builder.getConnection().createStatement();

            statement.executeUpdate("CREATE TABLE IF NOT EXISTS warps" +
                    "(id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                    "name VARCHAR(32)," +
                    "ind INTEGER DEFAULT -1," +
                    "displayName VARCHAR(64)," +
                    "material INTEGER DEFAULT 2," +
                    "lore VARCHAR(1024) NULL," +
                    "worldId INTEGER," +
                    "x INTEGER," +
                    "y INTEGER," +
                    "z INTEGER," +
                    "yaw FLOAT," +
                    "pitch FLOAT);");

            statement.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
