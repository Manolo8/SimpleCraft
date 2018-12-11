package com.github.manolo8.simplecraft.core.data.table;

import com.github.manolo8.simplecraft.core.data.model.base.DTO;

import java.lang.reflect.Field;
import java.math.BigDecimal;

public class Column<E extends Class> {

    protected String name;
    protected String type;
    protected boolean primary;
    protected boolean onlyInsert;
    protected Field field;
    protected Size size;

    protected int insertIndex;
    protected int updateIndex;

    public Column(String name, E type, boolean primary, boolean onlyInsert, Size size) {
        this.name = name;
        this.type = typeOf(type);
        this.size = size;
        this.primary = primary;
        this.onlyInsert = onlyInsert;
    }

    private static String typeOf(Class o) {
        if (o.isAssignableFrom(Integer.TYPE) || o == Integer.class) return "INTEGER";
        else if (o.isAssignableFrom(Double.TYPE) || o == Double.class) return "DOUBLE";
        else if (o.isAssignableFrom(Boolean.TYPE) || o == Boolean.class) return "BIT";
        else if (o.isAssignableFrom(String.class) || o == String.class) return "VARCHAR";
        else if (o.isAssignableFrom(BigDecimal.class) || o == BigDecimal.class) return "DECIMAL";
        else if (o.isAssignableFrom(Long.TYPE) || o == Long.class) return "BIGINT";
        else if (o.isAssignableFrom(Float.TYPE) || o == Float.class) return "FLOAT";
        else if (o.isAssignableFrom(Byte[].class) || o == Byte[].class || o == byte[].class) return "BLOB";
        return null;
    }

    private static SizeBuilder sizeOf0(String size, Size def) {
        if (def != null) {
            return new SizeBuilder(def);
        } else {
            switch (size) {
                case "INTEGER":
                    return new SizeBuilder(11);
                case "DOUBLE":
                    return new SizeBuilder(19, 12);
                case "BIT":
                    return new SizeBuilder(1);
                case "VARCHAR":
                    return new SizeBuilder(256);
                case "BIGINT":
                    return new SizeBuilder(64);
                case "DECIMAL":
                    return new SizeBuilder(32, 4);
                case "FLOAT":
                    return new SizeBuilder(32);
                case "BLOB":
                    return new SizeBuilder(512);
                default:
                    return null;
            }
        }
    }

    public SizeBuilder sizeOf(String size, Size def) {
        return sizeOf0(size, def);
    }

    public void append(StringBuilder builder) {
        if (primary) builder.append("id INTEGER PRIMARY KEY AUTO_INCREMENT");
        else {
            builder.append(name).append(" ").append(type).append(" ");
            builder.append(sizeOf(type, size));
        }
    }

    public void setValue(DTO dto, Object object) throws IllegalAccessException {
        if (object != null) {
            field.set(dto, object);
        }
    }
}
