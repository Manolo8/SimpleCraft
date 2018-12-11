package com.github.manolo8.simplecraft.core.data.connection;

import com.github.manolo8.simplecraft.core.data.model.base.DTO;
import com.github.manolo8.simplecraft.core.data.table.Column;
import com.github.manolo8.simplecraft.core.data.table.Size;
import com.github.manolo8.simplecraft.core.data.table.Table;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;

public class SQLite implements Database {

    private Connection connection;
    private File dataFolder;

    public String getHost() {
        return "jdbc:sqlite:" + dataFolder + "/simplecraft.db";
    }

    @Override
    public Connection getConnection() {
        try {

            if (connection != null && !connection.isClosed()) return connection;

            Class.forName("org.sqlite.JDBC");

            connection = DriverManager.getConnection(getHost());

        } catch (Exception e) {
            e.printStackTrace();
        }

        return connection;
    }

    public void setDataFolder(File dataFolder) {
        dataFolder.mkdir();
        this.dataFolder = dataFolder;
    }

    @Override
    public Table createTable(String name, Class<? extends DTO> clazz) {
        return new SQLiteTable(name, clazz);
    }

    class SQLiteTable extends Table {

        public SQLiteTable(String name, Class<? extends DTO> clazz) {
            super(name, clazz);
        }

        @Override
        protected Column createColumn(String name, Class type, boolean primary, boolean onlyInsert, Size size) {
            return new SQLiteColumn(name, type, primary, onlyInsert, size);
        }
    }

    class SQLiteColumn extends Column {

        public SQLiteColumn(String name, Class type, boolean primary, boolean onlyInsert, Size size) {
            super(name, type, primary, onlyInsert, size);
        }

        public void append(StringBuilder builder) {
            if (primary) builder.append("id INTEGER PRIMARY KEY AUTOINCREMENT");
            else {
                builder.append(name).append(" ").append(type).append(" ");
                builder.append(sizeOf(type, size));
            }
        }

        @Override
        public void setValue(DTO dto, Object object) throws IllegalAccessException {
            if (object != null) {
                if (type.equals("BIT")) {
                    int value = (int) object;
                    field.set(dto, value == 1);
                } else {
                    field.set(dto, object);
                }
            }
        }
    }
}
