package com.github.manolo8.simplecraft.core.data.connection;

import com.github.manolo8.simplecraft.core.data.model.base.DTO;
import com.github.manolo8.simplecraft.core.data.table.Table;

import java.sql.Connection;

public interface Database {

    Connection getConnection();

    Table createTable(String name, Class<? extends DTO> clazz);
}
