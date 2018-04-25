package com.github.manolo8.simplecraft.modules.shop.data;

import com.github.manolo8.simplecraft.data.builder.ConnectionBuilder;
import com.github.manolo8.simplecraft.modules.shop.Shop;
import org.bukkit.configuration.file.YamlConstructor;
import org.bukkit.configuration.file.YamlRepresenter;
import org.bukkit.inventory.ItemStack;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.nodes.Tag;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ShopDaoImpl implements ShopDao {

    private final ConnectionBuilder builder;
    private final String findOneQuery = "SELECT * FROM shops WHERE x=? AND y=? AND z=? AND world_id=?";
    private final String insertQuery = "INSERT INTO shops (x,y,z,world_id) VALUES (?,?,?,?)";
    private final String removeQuery = "DELETE FROM shops WHERE x=? AND y=? AND z=? AND world_id=?)";
    private final String updateQuery = "UPDATE shops SET owner_id=?,buy_price=?,sell_price=?,total_buy=?,total_sell=?,itemstack=? WHERE id=?";
    private final Yaml yaml;

    public ShopDaoImpl(ConnectionBuilder builder) {
        this.builder = builder;
        this.yaml = new Yaml(new YamlBukkitConstructor(), new YamlRepresenter(), new DumperOptions());

        defaults();
    }

    @Override
    public ShopDTO findOne(int x, int y, int z, int worldId) {
        try {
            PreparedStatement statement = builder.getConnection().prepareStatement(findOneQuery);

            statement.setInt(1, x);
            statement.setInt(2, y);
            statement.setInt(3, z);
            statement.setInt(4, worldId);

            ResultSet result = statement.executeQuery();

            if (!result.next()) return null;

            ShopDTO dto = fromResult(result);

            statement.close();

            return dto;

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void create(int x, int y, int z, int worldId) {
        try {
            PreparedStatement statement = builder.getConnection().prepareStatement(insertQuery);

            //INSERT INTO shops (x,y,z,world_id) VALUE VALUE (?,?,?,?)

            statement.setInt(1, x);
            statement.setInt(2, y);
            statement.setInt(3, z);
            statement.setInt(4, worldId);

            statement.executeUpdate();

            statement.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(Shop shop) {
        try {
            PreparedStatement statement = builder.getConnection().prepareStatement(removeQuery);

            //INSERT INTO shops (x,y,z,world_id) VALUE VALUE (?,?,?,?)

            statement.setInt(1, shop.getX());
            statement.setInt(2, shop.getY());
            statement.setInt(3, shop.getZ());
            statement.setInt(4, shop.getWorldId());

            statement.executeUpdate();

            statement.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void save(Shop shop) {
        try {
            PreparedStatement statement = builder.getConnection().prepareStatement(updateQuery);

            //UPDATE shops SET owner_id=?,buy_price=?,sell_price=?,total_buy=?,total_sell=?,itemstack=? WHERE id=?

            statement.setInt(1, shop.getOwner().getId());
            statement.setDouble(2, shop.getBuyPrice());
            statement.setDouble(3, shop.getSellPrice());
            statement.setInt(4, shop.getTotalBuy());
            statement.setInt(5, shop.getTotalSell());
            statement.setString(6, yaml.dump(shop.getItemStack()));
            statement.setInt(7, shop.getId());

            statement.executeUpdate();

            statement.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private ShopDTO fromResult(ResultSet result) throws SQLException {
        ShopDTO dto = new ShopDTO();

        dto.setId(result.getInt("id"));
        dto.setX(result.getInt("x"));
        dto.setY(result.getInt("y"));
        dto.setZ(result.getInt("z"));
        dto.setWorldId(result.getInt("world_id"));
        dto.setOwnerId(result.getInt("owner_id"));
        dto.setSellPrice(result.getDouble("sell_price"));
        dto.setBuyPrice(result.getDouble("buy_price"));
        dto.setTotalBuy(result.getInt("total_buy"));
        dto.setTotalSell(result.getInt("total_sell"));
        String itemstack = result.getString("itemstack");
        dto.setItemStack(itemstack == null || itemstack.isEmpty() ? null : yaml.loadAs(itemstack, ItemStack.class));

        return dto;
    }

    private void defaults() {
        try {
            Statement statement = builder.getConnection().createStatement();

            statement.executeUpdate("CREATE TABLE IF NOT EXISTS shops" +
                    "(id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "x INTEGER," +
                    "y INTEGER," +
                    "z INTEGER," +
                    "total_buy INTEGER," +
                    "total_sell INTEGER," +
                    "world_id INTEGER," +
                    "owner_id INTEGER," +
                    "buy_price DECIMAL(11,2)," +
                    "sell_price DECIMAL(11,2)," +
                    "itemstack BLOB);");

            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private class YamlBukkitConstructor extends YamlConstructor {
        public YamlBukkitConstructor() {
            this.yamlConstructors.put(new Tag(Tag.PREFIX + "org.bukkit.inventory.ItemStack"), yamlConstructors.get(Tag.MAP));
        }
    }
}
