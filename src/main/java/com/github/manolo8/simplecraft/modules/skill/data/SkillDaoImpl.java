package com.github.manolo8.simplecraft.modules.skill.data;

import com.github.manolo8.simplecraft.data.builder.ConnectionBuilder;
import com.github.manolo8.simplecraft.modules.skill.Skill;
import com.github.manolo8.simplecraft.modules.user.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class SkillDaoImpl implements SkillDao {

    private final ConnectionBuilder builder;
    private final String findByUserId = "SELECT * FROM skills WHERE owner_id=?";
    private final String insertQuery = "INSERT INTO skills (owner_id, type, level) VALUES (?,?,?)";
    private final String updateQuery = "UPDATE skills SET level=? WHERE id=?";

    public SkillDaoImpl(ConnectionBuilder builder) {
        this.builder = builder;

        defaults();
    }


    @Override
    public List<SkillDTO> findByUser(User user) {
        List<SkillDTO> list = new ArrayList<>();

        try {
            PreparedStatement statement = builder.getConnection().prepareStatement(findByUserId);

            statement.setInt(1, user.getId());

            ResultSet result = statement.executeQuery();


            while (result.next()) list.add(fromResult(result));

            statement.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    @Override
    public void save(Skill skill) {
        if (skill.isNew() || !skill.isNeedSave()) return;

        try {
            PreparedStatement statement = builder.getConnection().prepareStatement(updateQuery);

            statement.setInt(1, skill.getLevel());
            statement.setInt(2, skill.getId());

            statement.executeUpdate();

            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public SkillDTO create(User user, int type) {
        SkillDTO dto = new SkillDTO();
        dto.setType(type);
        dto.setLevel(0);

        try {
            PreparedStatement statement = builder.getConnection().prepareStatement(insertQuery);

            statement.setInt(1, user.getId());
            statement.setInt(2, type);
            statement.setInt(3, 0);

            statement.executeUpdate();

            int id = statement.getGeneratedKeys().getInt(1);

            dto.setId(id);

            statement.close();

            return dto;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return dto;
    }

    @Override
    public void save(List<Skill> skills) {
        try {
            PreparedStatement statement = builder.getConnection().prepareStatement(updateQuery);

            for (Skill skill : skills) {
                if (skill.isNew() || !skill.isNeedSave()) continue;

                statement.setInt(1, skill.getLevel());
                statement.setInt(2, skill.getId());
                statement.executeUpdate();

            }

            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private SkillDTO fromResult(ResultSet result) throws SQLException {
        SkillDTO dto = new SkillDTO();

        dto.setType(result.getInt("type"));
        dto.setLevel(result.getInt("level"));

        return dto;
    }

    private void defaults() {
        try {
            Statement statement = builder.getConnection().createStatement();

            statement.executeUpdate("CREATE TABLE IF NOT EXISTS skills" +
                    "(id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "owner_id INTEGER," +
                    "type INTEGER," +
                    "level INTEGER);");

            statement.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
