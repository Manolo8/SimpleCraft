package com.github.manolo8.simplecraft.core.data.connection;

import com.github.manolo8.simplecraft.core.data.model.base.DTO;
import com.github.manolo8.simplecraft.core.data.table.Column;
import com.github.manolo8.simplecraft.core.data.table.Size;
import com.github.manolo8.simplecraft.core.data.table.Table;

import java.sql.Connection;
import java.sql.DriverManager;

public class MySQL implements Database {
    private String host;
    private String username;
    private String password;
    private String dataBase;
    private Connection connection;

    public String getHost() {
        return "jdbc:mysql://"
                + host
                + "/" + dataBase
                + "?useUnicode=true&useSSL=false&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC&autoReconnect=true";
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDataBase() {
        return dataBase;
    }

    public void setDataBase(String dataBase) {
        this.dataBase = dataBase;
    }

    @Override
    public Connection getConnection() {
        try {

            if (connection != null && !connection.isClosed()) return connection;

            Class.forName("com.mysql.jdbc.Driver");

            connection = DriverManager.getConnection(getHost(), getUsername(), getPassword());

        } catch (Exception e) {
            e.printStackTrace();
        }

        return connection;
    }

    @Override
    public Table createTable(String name, Class<? extends DTO> clazz) {
        return new MysqlTable(name, clazz);
    }

    class MysqlTable extends Table {

        public MysqlTable(String name, Class<? extends DTO> clazz) {
            super(name, clazz);
        }

        @Override
        protected Column createColumn(String name, Class type, boolean primary, boolean onlyInsert, Size size) {
            return new MysqlColumn(name, type, primary, onlyInsert, size);
        }
    }

    class MysqlColumn extends Column {

        public MysqlColumn(String name, Class type, boolean primary, boolean onlyInsert, Size size) {
            super(name, type, primary, onlyInsert, size);
        }

        public void append(StringBuilder builder) {
            if (primary) builder.append("id INTEGER PRIMARY KEY AUTO_INCREMENT");
            else {
                builder.append(name).append(" ").append(type).append(" ");
                builder.append(sizeOf(type, size));
            }
        }

    }
}
