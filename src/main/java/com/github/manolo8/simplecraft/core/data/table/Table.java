package com.github.manolo8.simplecraft.core.data.table;

import com.github.manolo8.simplecraft.core.data.model.base.DTO;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class Table {

    private String name;
    private List<Column> columns;

    private String insertQuery;
    private String updateQuery;

    private int insertIndex;
    private int updateIndex;

    private Column primary;

    public Table(String name, Class<? extends DTO> clazz) {
        this.name = name;
        this.columns = new ArrayList<>();

        addColumns(clazz);
        build(clazz);
    }

    private void build(Class<? extends DTO> clazz) {
        insertQuery = insertQuery();
        updateQuery = updateQuery();

        List<Field> fields = getAllFields(new ArrayList<>(), clazz);

        for (Field field : fields) {
            Column column = getColumn(field.getName());
            column.field = field;
            field.setAccessible(true);
        }

        primary.insertIndex = columns.size();
    }

    private void addColumns(Class<? extends DTO> clazz) {
        List<Field> fields = getAllFields(new ArrayList<>(), clazz);

        insertIndex = 1;
        updateIndex = 1;

        for (Field field : fields) {

            String name = field.getName();
            Class type = field.getType();

            boolean primary = name.equals("id");
            boolean onlyInsert = field.isAnnotationPresent(OnlyInsert.class);

            Column column = createColumn(name, type, primary, onlyInsert, field.getDeclaredAnnotation(Size.class));

            if (!primary) {
                column.insertIndex = insertIndex++;
                if (!onlyInsert) column.updateIndex = updateIndex++;
            }

            this.columns.add(column);
            if (primary) this.primary = column;
        }

        primary.updateIndex = updateIndex;
    }

    protected Column createColumn(String name, Class type, boolean primary, boolean onlyInsert, Size size) {
        return new Column(name, type, primary, onlyInsert, size);
    }

    private Column getColumn(String name) {
        for (Column column : columns) {
            if (column.name.equals(name)) return column;
        }

        return null;
    }

    private List<Field> getAllFields(List<Field> fields, Class<?> type) {
        List<Field> add = new ArrayList<>();

        for (Field field : type.getDeclaredFields()) {
            if (Modifier.isFinal(field.getModifiers())) continue;
            add.add(field);
        }

        fields.addAll(0, add);

        if (type.getSuperclass() != null) {
            getAllFields(fields, type.getSuperclass());
        }

        return fields;
    }

    public String createTableQuery() {
        StringBuilder builder = new StringBuilder();

        builder.append("CREATE TABLE IF NOT EXISTS ").append(name)
                .append(" (");

        for (int i = 0; i < columns.size(); i++) {
            Column column = columns.get(i);
            column.append(builder);
            if (i != columns.size() - 1) builder.append(",");
        }

        builder.append(");");

        return builder.toString();
    }

    private String insertQuery() {
        StringBuilder builder = new StringBuilder();

        builder.append("INSERT INTO ").append(name).append(" (");

        for (Column column : columns) {
            //0 = 1 (me entendes '-')
            if (column.primary) continue;

            builder.append(column.name);
            if (column.insertIndex != insertIndex - 1) builder.append(",");
        }

        builder.append(") VALUES (");

        for (Column column : columns) {
            //0 = 1 (me entendes '-')
            if (column.primary) continue;

            builder.append("?");
            if (column.insertIndex != insertIndex - 1) builder.append(",");
        }

        builder.append(");");

        return builder.toString();
    }

    private String updateQuery() {
        StringBuilder builder = new StringBuilder();

        builder.append("UPDATE ").append(name).append(" SET ");

        for (Column column : columns) {
            if (column.primary || column.onlyInsert) continue;

            builder.append(column.name).append("=").append("?");
            if (column.updateIndex != updateIndex - 1) builder.append(",");
        }

        builder.append(" WHERE id=?");

        return builder.toString();
    }

    public String getInsertQuery() {
        return insertQuery;
    }

    public String getUpdateQuery() {
        return updateQuery;
    }

    public void insert(DTO dto, PreparedStatement statement) {
        try {
            for (Column column : columns) {
                if (!column.primary) {
                    statement.setObject(column.insertIndex, column.field.get(dto));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void update(DTO dto, PreparedStatement statement) {
        try {
            for (Column column : columns) {
                if (!column.primary && !column.onlyInsert) {
                    statement.setObject(column.updateIndex, column.field.get(dto));
                }
            }
            statement.setObject(primary.updateIndex, primary.field.get(dto));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setValues(DTO dto, ResultSet result) {
        try {
            for (Column column : columns) {
                column.setValue(dto, result.getObject(column.name));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
