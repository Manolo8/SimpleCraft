package com.github.manolo8.simplecraft.domain.user.data;

import com.github.manolo8.simplecraft.data.builder.ConnectionBuilder;
import com.github.manolo8.simplecraft.data.dao.UserDao;
import com.github.manolo8.simplecraft.domain.group.Group;
import com.github.manolo8.simplecraft.domain.user.User;
import org.bukkit.Bukkit;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

public class UserDaoImpl implements UserDao {

    private final ConnectionBuilder builder;
    private final String findOneQuery = "SELECT * FROM users WHERE id=?;";
    private final String findOneQueryByUuid = "SELECT * FROM users WHERE uuid=?;";
    private final String findOneByNameQuery = "SELECT * FROM users WHERE name=?;";
    private final String insertQuery = "INSERT INTO users (uuid) VALUES (?);";
    private final String updateQuery = "UPDATE users SET name=?,money=?,group_id=? WHERE id=?;";

    public UserDaoImpl(ConnectionBuilder builder) {
        this.builder = builder;

        defaults();
    }

    @Override
    public UserDTO findOne(UUID uuid) {
        try {
            PreparedStatement statement = builder.getConnection().prepareStatement(findOneQueryByUuid);

            statement.setString(1, uuid.toString());

            ResultSet resultSet = statement.executeQuery();

            UserDTO userDTO;

            if (!resultSet.next()) userDTO = create(uuid);
            else userDTO = fromResultSet(resultSet);

            Bukkit.getLogger().info("User " + uuid + " founded?");

            statement.close();

            return userDTO;

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public UserDTO findOne(Integer id) {
        try {
            PreparedStatement statement = builder.getConnection().prepareStatement(findOneQuery);

            statement.setInt(1, id);

            ResultSet result = statement.executeQuery();

            UserDTO userDTO;
            if (result.next()) userDTO = fromResultSet(result);
            else userDTO = null;

            Bukkit.getLogger().info("User with id " + id + " founded?");

            statement.close();

            return userDTO;

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public UserDTO findOne(String lastName) {
        try {
            PreparedStatement statement = builder.getConnection().prepareStatement(findOneByNameQuery);

            statement.setString(1, lastName);

            ResultSet resultSet = statement.executeQuery();


            if (!resultSet.next()) return null;

            UserDTO userDTO = fromResultSet(resultSet);

            Bukkit.getLogger().info("User " + lastName + " founded?");

            statement.close();

            return userDTO;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    private UserDTO create(UUID uuid) {
        try {
            PreparedStatement statement = builder.getConnection().prepareStatement(insertQuery);

            UserDTO userDTO = new UserDTO();
            userDTO.setUuid(uuid);

            statement.setString(1, uuid.toString());

            statement.executeUpdate();

            int id = statement.getGeneratedKeys().getInt(1);

            userDTO.setId(id);

            statement.close();

            Bukkit.getLogger().info("User with " + uuid + " created");

            return userDTO;

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void save(User user) {
        try {
            PreparedStatement statement = builder.getConnection().prepareStatement(updateQuery);

            //UPDATE users SET name=?,money=?,group_uuid=? WHERE id=?;

            statement.setString(1, user.getName());
            statement.setDouble(2, user.getMoney());
            Group group = user.getGroup();
            statement.setInt(3, group == null ? 0 : group.getId());
            statement.setInt(4, user.getId());

            Bukkit.getLogger().info("User " + user.getName() + " saved");

            statement.executeUpdate();

            statement.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void defaults() {
        try {
            Statement statement = builder.getConnection().createStatement();

            statement.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS users " +
                            "(id INTEGER PRIMARY KEY AUTOINCREMENT," +
                            "uuid VARCHAR(36) NOT NULL," +
                            "name VARCHAR(32)," +
                            "group_id INT(11)," +
                            "money DECIMAL(16,3));"
            );

            statement.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private UserDTO fromResultSet(ResultSet result) throws SQLException {
        UserDTO userDTO = new UserDTO();

        userDTO.setId(result.getInt("id"));
        userDTO.setUuid(UUID.fromString(result.getString("uuid")));
        userDTO.setName(result.getString("name"));
        userDTO.setMoney(result.getDouble("money"));
        userDTO.setGroupId(result.getInt("group_id"));

        return userDTO;
    }
}
