package com.github.manolo8.simplecraft.core.data.model.base;

import java.sql.SQLException;
import java.util.List;

public interface Repository<E extends BaseEntity> {

    E findOne(int id) throws SQLException;

    List<E> findAll() throws SQLException;

    void save(E entity) throws SQLException;

    void delete(E entity) throws SQLException;
}
