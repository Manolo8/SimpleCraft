package com.github.manolo8.simplecraft.module.machine.fuel;

import com.github.manolo8.simplecraft.core.data.model.base.BaseDAO;
import com.github.manolo8.simplecraft.core.data.model.base.BaseLoader;
import com.github.manolo8.simplecraft.core.data.model.base.BaseRepository;
import com.github.manolo8.simplecraft.core.data.model.base.DTO;
import com.github.manolo8.simplecraft.core.data.cache.SaveCache;
import com.github.manolo8.simplecraft.core.data.connection.Database;

import java.sql.SQLException;

public class FuelRepository extends BaseRepository<Fuel,
        FuelRepository.FuelDTO,
        FuelRepository.FuelDAO,
        FuelRepository.FuelCache,
        FuelRepository.FuelLoader> {

    //======================================================
    //======================REPOSITORY======================
    //======================================================
    public FuelRepository(Database database) {
        super(database);
    }

    @Override
    protected FuelDAO initDao() throws SQLException {
        return new FuelDAO(database);
    }

    @Override
    protected FuelLoader initLoader() {
        return new FuelLoader();
    }

    @Override
    protected FuelCache initCache() {
        return new FuelCache(this);
    }
    //======================================================
    //=====================_REPOSITORY======================
    //======================================================


    //======================================================
    //==========================DTO=========================
    //======================================================
    class FuelDTO extends DTO {
    }
    //======================================================
    //=========================_DTO=========================
    //======================================================


    //======================================================
    //==========================DAO=========================
    //======================================================
    class FuelDAO extends BaseDAO<FuelDTO> {

        FuelDAO(Database database) throws SQLException {
            super(database, "Fuels", FuelDTO.class);
        }

    }
    //======================================================
    //=========================_DAO=========================
    //======================================================

    //======================================================
    //==========================CACHE=======================
    //======================================================
    class FuelCache extends SaveCache<Fuel, FuelRepository> {

        FuelCache(FuelRepository repository) {
            super(repository);
        }
    }
    //======================================================
    //=========================_CACHE=======================
    //======================================================


    //======================================================
    //=========================LOADER=======================
    //======================================================
    class FuelLoader extends BaseLoader<Fuel, FuelDTO> {

        @Override
        public Fuel newEntity() {
            return new Fuel();
        }

        @Override
        public Fuel fromDTO(FuelDTO dto) throws SQLException {
            Fuel entity = super.fromDTO(dto);

            return entity;
        }

        @Override
        public FuelDTO toDTO(Fuel entity) throws SQLException {
            FuelDTO dto = super.toDTO(entity);

            return dto;
        }
    }
    //======================================================
    //========================_LOADER=======================
    //======================================================
}
