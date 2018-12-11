package com.github.manolo8.simplecraft.core.data.model.named;

import com.github.manolo8.simplecraft.core.data.connection.Database;
import com.github.manolo8.simplecraft.core.data.model.base.BaseDAO;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public abstract class NamedDAO<O extends NamedDTO> extends BaseDAO<O> {

    private final String findByNameQuery;
    private final String findNameListQuery;
    private final String findAllByNameStartWithQuery;

    protected NamedDAO(Database database, String name, Class<O> oClass) throws SQLException {
        super(database, name, oClass);
        findByNameQuery = "SELECT * FROM " + name + " WHERE fastName=?";
        findNameListQuery = "SELECT fastName FROM " + name + " WHERE fastName like ? LIMIT ?";
        findAllByNameStartWithQuery = "SELECT * FROM " + name + " WHERE fastName like ?";
    }

    public O findByName(String name) throws SQLException {
        PreparedStatement statement = prepareStatement(findByNameQuery);

        statement.setString(1, name);

        ResultSet result = statement.executeQuery();

        O o = result.next() ? fromResult(result) : null;

        statement.close();

        return o;
    }

    public List<Integer> findAllByNameStartWith(String name) throws SQLException {
        PreparedStatement statement = prepareStatement(findNameListQuery);

        statement.setString(1, name + "%");

        ResultSet result = statement.executeQuery();

        List<Integer> dtos = new ArrayList<>();

        while (result.next()) dtos.add(result.getInt(1));

        statement.close();

        return dtos;
    }

    public List<String> findNameList(String name, int limit) throws SQLException {
        PreparedStatement statement = prepareStatement(findNameListQuery);

        statement.setString(1, name + "%");
        statement.setInt(2, limit);

        ResultSet result = statement.executeQuery();

        List<String> names = new ArrayList<>();

        while (result.next()) names.add(result.getString(1));

        statement.close();

        return names;
    }
}
