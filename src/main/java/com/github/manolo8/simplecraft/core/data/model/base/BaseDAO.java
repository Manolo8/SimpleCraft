package com.github.manolo8.simplecraft.core.data.model.base;

import com.github.manolo8.simplecraft.core.data.connection.Database;
import com.github.manolo8.simplecraft.core.data.table.Table;
import com.github.manolo8.simplecraft.utils.reflection.ReflectionUtils;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public abstract class BaseDAO<O extends DTO> implements DAO<O> {

    protected final Database database;
    protected final String name;

    private final String findAllQuery;
    private final String findOneQuery;
    private final String deleteOneQuery;
    private final String findByIdInQuery;
    private final String countQuery;

    private final Class<O> clazz;

    protected Table table;

    protected BaseDAO(Database database, String name, Class<O> clazz) throws SQLException {
        this.database = database;
        this.name = name;
        this.clazz = clazz;

        this.findAllQuery = "SELECT id FROM " + name;
        this.findOneQuery = "SELECT * FROM " + name + " WHERE id=?";
        this.deleteOneQuery = "DELETE FROM " + name + " WHERE id=?";
        this.findByIdInQuery = "SELECT * FROM " + name + " WHERE id in (";
        this.countQuery = "SELECT count(id) FROM " + name;

        init(clazz);
    }

    protected void init(Class<O> clazz) throws SQLException {

        table = database.createTable(name, clazz);

        Statement statement = createStatement();

        statement.executeUpdate(table.createTableQuery());

        statement.close();
    }

    protected O newInstance() {
        return ReflectionUtils.createConstructor(clazz, this);
    }

    public O fromResult(ResultSet result) {
        O o = newInstance();

        table.setValues(o, result);

        return o;
    }

    public List<O> fromResultList(ResultSet result) throws SQLException {
        List<O> list = new ArrayList<>();

        while (result.next()) list.add(fromResult(result));

        return list;
    }

    protected List<Integer> fromResultListId(PreparedStatement statement) throws SQLException {
        ResultSet result = statement.executeQuery();

        List<Integer> ids = new ArrayList<>();

        while (result.next()) ids.add(result.getInt(1));

        statement.close();

        return ids;
    }

    @Override
    public void save(O dto) throws SQLException {
        PreparedStatement statement = prepareStatement(table.getUpdateQuery());

        table.update(dto, statement);

        statement.executeUpdate();

        statement.close();
    }

    @Override
    public O findOne(int id) throws SQLException {
        PreparedStatement statement = prepareStatement(findOneQuery);

        statement.setInt(1, id);

        ResultSet result = statement.executeQuery();

        O dto = result.next() ? fromResult(result) : null;

        statement.close();

        return dto;
    }

    @Override
    public List<O> findByIdIn(List<Integer> ids) throws SQLException {

        int iMax = ids.size() - 1;

        StringBuilder b = new StringBuilder(findByIdInQuery);
        for (int i = 0; ; i++) {
            b.append(ids.get(i));
            if (i == iMax) {
                b.append(")");
                break;
            }
            b.append(",");
        }

        Statement statement = createStatement();

        ResultSet result = statement.executeQuery(b.toString());

        List<O> dtos = fromResultList(result);

        statement.close();

        return dtos;
    }

    @Override
    public List<Integer> findAll() throws SQLException {
        Statement statement = createStatement();

        ResultSet result = statement.executeQuery(findAllQuery);

        List<Integer> dtos = new ArrayList<>();

        while (result.next()) dtos.add(result.getInt(1));

        statement.close();

        return dtos;
    }

    public int count() throws SQLException {
        Statement statement = createStatement();

        ResultSet result = statement.executeQuery(countQuery);

        int count = 0;

        if (result.next()) count = result.getInt(1);

        statement.close();

        return count;
    }

    @Override
    public O create(O dto) throws SQLException {
        PreparedStatement statement = database.getConnection().prepareStatement(table.getInsertQuery(), Statement.RETURN_GENERATED_KEYS);

        if (dto == null) dto = newInstance();

        table.insert(dto, statement);

        statement.executeUpdate();

        ResultSet result = statement.getGeneratedKeys();

        if (result.next()) {
            dto.id = result.getInt(1);
        } else throw new SQLException();

        statement.close();

        return dto;
    }

    @Override
    public void delete(int id) throws SQLException {
        PreparedStatement statement = prepareStatement(deleteOneQuery);

        statement.setInt(1, id);

        statement.executeUpdate();

        statement.close();
    }

    protected PreparedStatement prepareStatement(String query) throws SQLException {
        return database.getConnection().prepareStatement(query);
    }

    protected Statement createStatement() throws SQLException {
        return database.getConnection().createStatement();
    }
}
