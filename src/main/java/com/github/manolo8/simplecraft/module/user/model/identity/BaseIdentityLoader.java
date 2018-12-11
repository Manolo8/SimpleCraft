package com.github.manolo8.simplecraft.module.user.model.identity;

import com.github.manolo8.simplecraft.core.data.model.base.BaseLoader;
import com.github.manolo8.simplecraft.module.user.identity.IdentityRepository;

import java.sql.SQLException;

public abstract class BaseIdentityLoader<E extends BaseIdentity, O extends BaseIdentityDTO> extends BaseLoader<E, O> {

    private final IdentityRepository identityRepository;

    public BaseIdentityLoader(IdentityRepository identityRepository) {
        this.identityRepository = identityRepository;
    }

    @Override
    public E fromDTO(O dto) throws SQLException {
        E entity = super.fromDTO(dto);

        entity.setIdentity(identityRepository.findOne(dto.getIdentityId()));

        return entity;
    }

    @Override
    public O toDTO(E entity) throws SQLException {
        O dto = super.toDTO(entity);

        dto.setIdentityId(entity.getIdentity().getId());

        return dto;
    }
}
