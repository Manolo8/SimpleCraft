package com.github.manolo8.simplecraft.module.skin;

import com.github.manolo8.simplecraft.core.data.connection.Database;
import com.github.manolo8.simplecraft.core.data.model.named.*;
import com.github.manolo8.simplecraft.core.data.table.Size;

import java.sql.SQLException;

public class SkinRepository extends NamedRepository<Skin,
        SkinRepository.SkinDTO,
        SkinRepository.SkinDAO,
        SkinRepository.SkinCache,
        SkinRepository.SkinLoader> {

    //======================================================
    //======================REPOSITORY======================
    //======================================================
    public SkinRepository(Database database) {
        super(database);
    }

    @Override
    protected SkinDAO initDao() throws SQLException {
        return new SkinDAO(database);
    }

    @Override
    protected SkinLoader initLoader() {
        return new SkinLoader();
    }

    @Override
    protected SkinCache initCache() {
        return new SkinCache(this);
    }

    public Skin fromData(String[] data) throws SQLException {
        SkinDTO dto = new SkinDTO();

        dto.name = data[0];
        dto.fastName = data[0].toLowerCase();
        dto.value = data[1];
        dto.signature = data[2];

        return create(dto);
    }
    //======================================================
    //=====================_REPOSITORY======================
    //======================================================


    //======================================================
    //==========================DTO=========================
    //======================================================
    class SkinDTO extends NamedDTO {

        @Size(512)
        private String value;
        @Size(1024)
        private String signature;
    }
    //======================================================
    //=========================_DTO=========================
    //======================================================


    //======================================================
    //==========================DAO=========================
    //======================================================
    class SkinDAO extends NamedDAO<SkinDTO> {

        SkinDAO(Database database) throws SQLException {
            super(database, "Skins", SkinDTO.class);
        }

    }
    //======================================================
    //=========================_DAO=========================
    //======================================================

    //======================================================
    //==========================CACHE=======================
    //======================================================
    class SkinCache extends NamedCache<Skin, SkinRepository> {

        SkinCache(SkinRepository repository) {
            super(repository);
        }
    }
    //======================================================
    //=========================_CACHE=======================
    //======================================================


    //======================================================
    //=========================LOADER=======================
    //======================================================
    class SkinLoader extends NamedLoader<Skin, SkinDTO> {

        @Override
        public Skin newEntity() {
            return new Skin();
        }

        @Override
        public Skin fromDTO(SkinDTO dto) throws SQLException {
            Skin entity = super.fromDTO(dto);

            entity.setValue(dto.value);
            entity.setSignature(dto.signature);

            return entity;
        }

        @Override
        public SkinDTO toDTO(Skin entity) throws SQLException {
            SkinDTO dto = super.toDTO(entity);

            dto.value = entity.getValue();
            dto.signature = entity.getSignature();

            return dto;
        }
    }
    //======================================================
    //========================_LOADER=======================
    //======================================================
}
