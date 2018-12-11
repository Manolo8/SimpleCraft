package com.github.manolo8.simplecraft.module.user.model.identity;

import com.github.manolo8.simplecraft.core.data.model.base.BaseDAO;
import com.github.manolo8.simplecraft.core.data.connection.Database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public abstract class BaseIdentityDAO<O extends BaseIdentityDTO> extends BaseDAO<O> {

    private final String findByIdentityIdQuery;

    protected BaseIdentityDAO(Database database, String name, Class<O> oClass) throws SQLException {
        super(database, name, oClass);
        findByIdentityIdQuery = "SELECT * FROM " + name + " WHERE identityId=?";
    }

    public O findByIdentity(int identityId) throws SQLException {
        PreparedStatement statement = prepareStatement(findByIdentityIdQuery);

        statement.setInt(1, identityId);

        ResultSet result = statement.executeQuery();

        O o = result.next() ? fromResult(result) : null;

        statement.close();

        return o;
    }
}
