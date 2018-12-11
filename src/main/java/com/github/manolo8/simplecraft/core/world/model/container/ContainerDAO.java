package com.github.manolo8.simplecraft.core.world.model.container;

import com.github.manolo8.simplecraft.core.world.container.ChunkContainer;
import com.github.manolo8.simplecraft.core.data.connection.Database;
import com.github.manolo8.simplecraft.core.data.model.named.NamedDAO;
import org.intellij.lang.annotations.Language;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public abstract class ContainerDAO<O extends ContainerDTO> extends NamedDAO<O> {

    @Language("GenericSQL")
    private final String findByContainerQuery;

    protected ContainerDAO(Database database, String name, Class<O> oClass) throws SQLException {
        super(database, name, oClass);

        findByContainerQuery = "SELECT id FROM " + name +
                " WHERE worldId=? AND maxX >= ? AND maxZ >= ? AND ? >= minX AND ? >= minZ;";
    }

    protected List<Integer> findByContainer(ChunkContainer container, int worldId) throws SQLException {
        PreparedStatement statement = prepareStatement(findByContainerQuery);

        statement.setInt(1, worldId);
        statement.setInt(2, container.x * 16);
        statement.setInt(3, container.z * 16);
        statement.setInt(4, container.x * 16 + 15);
        statement.setInt(5, container.z * 16 + 15);

        List<Integer> list = new ArrayList<>();

        ResultSet result = statement.executeQuery();

        while (result.next()) list.add(result.getInt(1));

        statement.close();

        return list;
    }
}
