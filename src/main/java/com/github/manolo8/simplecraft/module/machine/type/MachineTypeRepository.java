package com.github.manolo8.simplecraft.module.machine.type;

import com.github.manolo8.simplecraft.core.data.connection.Database;
import com.github.manolo8.simplecraft.core.data.model.named.*;
import com.github.manolo8.simplecraft.utils.mc.MaterialList;

import java.sql.SQLException;

public class MachineTypeRepository extends NamedRepository<MachineType,
        MachineTypeRepository.SpawnerTypeDTO,
        MachineTypeRepository.SpawnerTypeDAO,
        MachineTypeRepository.SpawnerTypeCache,
        MachineTypeRepository.SpawnerTypeLoader> {

    //======================================================
    //======================REPOSITORY======================
    //======================================================
    public MachineTypeRepository(Database database) {
        super(database);
    }

    @Override
    protected SpawnerTypeDAO initDao() throws SQLException {
        return new SpawnerTypeDAO(database);
    }

    @Override
    protected SpawnerTypeLoader initLoader() {
        return new SpawnerTypeLoader();
    }

    @Override
    protected SpawnerTypeCache initCache() {
        return new SpawnerTypeCache(this);
    }
    //======================================================
    //=====================_REPOSITORY======================
    //======================================================


    //======================================================
    //==========================DTO=========================
    //======================================================
    class SpawnerTypeDTO extends NamedDTO {

        private int material;
        private int minFuelLevel;
        private double amplifier;

    }
    //======================================================
    //=========================_DTO=========================
    //======================================================


    //======================================================
    //==========================DAO=========================
    //======================================================
    class SpawnerTypeDAO extends NamedDAO<SpawnerTypeDTO> {

        SpawnerTypeDAO(Database database) throws SQLException {
            super(database, "MachineTypes", SpawnerTypeDTO.class);
        }

    }
    //======================================================
    //=========================_DAO=========================
    //======================================================

    //======================================================
    //==========================CACHE=======================
    //======================================================
    class SpawnerTypeCache extends NamedCache<MachineType, MachineTypeRepository> {

        SpawnerTypeCache(MachineTypeRepository repository) {
            super(repository);
        }
    }
    //======================================================
    //=========================_CACHE=======================
    //======================================================


    //======================================================
    //=========================LOADER=======================
    //======================================================
    @SuppressWarnings("deprecation")
    class SpawnerTypeLoader extends NamedLoader<MachineType, SpawnerTypeDTO> {

        @Override
        public MachineType newEntity() {
            return new MachineType();
        }

        @Override
        public MachineType fromDTO(SpawnerTypeDTO dto) throws SQLException {
            MachineType entity = super.fromDTO(dto);

            entity.setAmplifier(dto.amplifier);
            entity.setMinFuelLevel(dto.minFuelLevel);
            entity.setMaterial(MaterialList.fromId(dto.material));

            return entity;
        }

        @Override
        public SpawnerTypeDTO toDTO(MachineType entity) throws SQLException {
            SpawnerTypeDTO dto = super.toDTO(entity);

            dto.amplifier = entity.getAmplifier();
            dto.minFuelLevel = entity.getMinFuelLevel();
            dto.material = MaterialList.toId(entity.getMaterial());

            return dto;
        }
    }
    //======================================================
    //========================_LOADER=======================
    //======================================================
}
