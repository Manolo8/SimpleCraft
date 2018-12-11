package com.github.manolo8.simplecraft.core.data.model.base;

import com.github.manolo8.simplecraft.utils.reflection.ReflectionUtils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.SQLException;

public abstract class BaseLoader<E extends BaseEntity, O extends DTO> implements Loader<E, O> {

    private Class<O> oClass;

    public BaseLoader() {
        Type[] type = ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments();

        oClass = (Class<O>) type[1];
    }

    public abstract E newEntity();

    public O newDTO() {
        return ReflectionUtils.createConstructor(oClass, this);
    }

    public E fromDTO(O dto) throws SQLException {
        E entity = newEntity();

        entity.setId(dto.id);

        return entity;
    }

    public O toDTO(E entity) throws SQLException {
        O dto = newDTO();

        dto.id = entity.getId();

        return dto;
    }
}
