package com.github.manolo8.simplecraft.domain.group.data;

import com.github.manolo8.simplecraft.data.builder.ConnectionBuilder;
import com.github.manolo8.simplecraft.data.dao.GroupDao;
import com.github.manolo8.simplecraft.domain.group.Group;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class GroupDaoImpl implements GroupDao {

    private final ConnectionBuilder builder;
    private final String findOneQuery = "SELECT * FROM groups AS g LEFT JOIN group_permissions AS gp ON g.id=gp.group_id WHERE id=?";
    private final String findDefaultQuery = "SELECT * FROM groups AS g LEFT JOIN group_permissions AS gp ON g.id=gp.group_id WHERE isDefault=1";
    private final String findOneByNameQuery = "SELECT * FROM groups AS g LEFT JOIN group_permissions AS gp ON g.id=gp.group_id WHERE name=?";
    private final String insertQuery = "INSERT INTO groups (id, name, parent_id, isDefault) VALUES (?, ?, ?, ?)";
    private final String deleteQuery = "DELETE FROM groups WHERE id=?";
    private final String deletePermissionQuery = "DELETE FROM group_permissions WHERE group_id=?";
    private final String updateQuery = "UPDATE groups SET name=?,parent_id=?,isDefault=?,tag=? WHERE id=?";
    private final String findPermissionQuery = "SELECT * FROM group_permissions WHERE group_id=?";
    private final String addPermissionQuery = "INSERT INTO group_permissions (group_id, permission) VALUES (?, ?)";

    public GroupDaoImpl(ConnectionBuilder builder) {
        this.builder = builder;

        defaults();
    }

    @Override
    public GroupDTO findOne(Integer id) {
        try {
            PreparedStatement statement = builder.getConnection().prepareStatement(findOneQuery);

            statement.setInt(1, id);

            ResultSet result = statement.executeQuery();

            if (!result.next()) return null;

            GroupDTO groupDTO = fromResult(result);

            statement.close();

            return groupDTO;

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public GroupDTO findOne(String name) {
        try {
            PreparedStatement statement = builder.getConnection().prepareStatement(findOneByNameQuery);

            statement.setString(1, name);

            ResultSet result = statement.executeQuery();

            if (!result.next()) return null;

            GroupDTO groupDTO = fromResult(result);

            statement.close();

            return groupDTO;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public GroupDTO findDefaultGroup() {
        try {
            PreparedStatement statement = builder.getConnection().prepareStatement(findDefaultQuery);

            ResultSet result = statement.executeQuery();

            if (!result.next()) return null;

            GroupDTO groupDTO = fromResult(result);

            statement.close();

            return groupDTO;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public GroupDTO create(String name) {
        try {
            PreparedStatement statement = builder.getConnection().prepareStatement(insertQuery);

            GroupDTO groupDTO = new GroupDTO();
            groupDTO.setPermissions(new ArrayList<>());

            statement.setString(2, name);
            statement.setString(3, null);
            statement.setBoolean(4, false);

            statement.executeUpdate();

            groupDTO.setId(statement.getGeneratedKeys().getInt(1));
            groupDTO.setName(name);

            statement.close();

            return groupDTO;

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

            statement = builder.getConnection().prepareStatement(deletePermissionQuery);

            statement.setInt(1, id);

            statement.executeUpdate();
            statement.close();


        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void save(Group group) {
        try {
            PreparedStatement statement = builder.getConnection().prepareStatement(updateQuery);

            //UPDATE groups SET name=?,parent=?,isDefault=? WHERE id=?

            statement.setString(1, group.getName());
            statement.setInt(2, group.getParent() == null ? 0 : group.getParent().getId());
            statement.setBoolean(3, group.isDefault());
            statement.setString(4, group.getTag());
            statement.setInt(5, group.getId());

            statement.executeUpdate();
            statement.close();

            statement = builder.getConnection().prepareStatement(findPermissionQuery);

            //SELECT * FROM group_permissions WHERE group_id=?

            statement.setInt(1, group.getId());

            ResultSet result = statement.executeQuery();

            List<String> permissions = group.getPermissions();
            List<String> permissionsToAdd = new ArrayList<>(permissions);

            while (result.next()) {
                String current = result.getString("permission");
                if (!permissionsToAdd.remove(current)) result.deleteRow();
            }

            statement.close();

            if (permissionsToAdd.isEmpty()) return;

            //INSERT INTO group_permissions (group_id, permission) VALUE (?, ?)

            statement = builder.getConnection().prepareStatement(addPermissionQuery);
            statement.setInt(1, group.getId());

            for (String string : permissionsToAdd) {
                statement.setString(2, string);
                statement.executeUpdate();
            }

            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private GroupDTO fromResult(ResultSet result) throws SQLException {
        GroupDTO groupDTO = new GroupDTO();

        groupDTO.setId(result.getInt("id"));
        groupDTO.setName(result.getString("name"));
        groupDTO.setTag(result.getString("tag"));
        groupDTO.setParentId(result.getInt("parent_id"));

        List<String> permissions = new ArrayList();

        do {
            String permission = result.getString("permission");
            if (permission == null) continue;
            permissions.add(result.getString("permission"));
        } while (result.next());

        groupDTO.setPermissions(permissions);

        return groupDTO;
    }

    private void defaults() {
        try {
            Statement statement = builder.getConnection().createStatement();

            statement.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS groups" +
                            "(id INTEGER PRIMARY KEY AUTOINCREMENT," +
                            "name VARCHAR(32)," +
                            "tag VARCHAR(16)," +
                            "parent_id VARCHAR(36)," +
                            "isDefault BIT(1));" +
                            "CREATE TABLE IF NOT EXISTS group_permissions" +
                            "(group_id VARCHAR(36) NOT NULL," +
                            "permission VARCHAR(64) NOT NULL);");

            statement.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
