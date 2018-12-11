package com.github.manolo8.simplecraft.module.region;

import com.github.manolo8.simplecraft.core.world.WorldInfoRepository;
import com.github.manolo8.simplecraft.core.data.connection.Database;
import com.github.manolo8.simplecraft.core.world.model.container.ContainerDAO;
import com.github.manolo8.simplecraft.core.world.model.container.ContainerDTO;
import com.github.manolo8.simplecraft.core.world.model.container.ContainerLoader;
import com.github.manolo8.simplecraft.core.world.model.container.ContainerRepository;
import com.github.manolo8.simplecraft.core.data.model.named.NamedCache;
import com.github.manolo8.simplecraft.core.data.table.Size;

import java.sql.SQLException;

public class RegionRepository extends ContainerRepository<Region,
        RegionRepository.RegionDTO,
        RegionRepository.RegionDAO,
        RegionRepository.RegionCache,
        RegionRepository.RegionLoader> {

    //======================================================
    //======================REPOSITORY======================
    //======================================================

    private final WorldInfoRepository worldInfoRepository;

    public RegionRepository(Database database, WorldInfoRepository worldInfoRepository) {
        super(database);

        this.worldInfoRepository = worldInfoRepository;
    }

    @Override
    protected RegionDAO initDao() throws SQLException {
        return new RegionDAO(database);
    }

    @Override
    protected RegionLoader initLoader() {
        return new RegionLoader();
    }

    @Override
    protected RegionCache initCache() {
        return new RegionCache(this);
    }
    //======================================================
    //=====================_REPOSITORY======================
    //======================================================


    //======================================================
    //==========================DTO=========================
    //======================================================
    class RegionDTO extends ContainerDTO {

        @Size(2)
        private byte[] flag;
        private int minRank;
    }
    //======================================================
    //=========================_DTO=========================
    //======================================================


    //======================================================
    //==========================DAO=========================
    //======================================================
    class RegionDAO extends ContainerDAO<RegionDTO> {

        protected RegionDAO(Database database) throws SQLException {
            super(database, "Regions", RegionDTO.class);
        }

    }
    //======================================================
    //=========================_DAO=========================
    //======================================================


    //======================================================
    //==========================CACHE=======================
    //======================================================
    class RegionCache extends NamedCache<Region, RegionRepository> {

        public RegionCache(RegionRepository repository) {
            super(repository);
        }
    }
    //======================================================
    //=========================_CACHE=======================
    //======================================================

    //======================================================
    //=========================LOADER=======================
    //======================================================
    class RegionLoader extends ContainerLoader<Region, RegionDTO> {

        RegionLoader() {
            super(worldInfoRepository);
        }

        @Override
        public Region newEntity() {
            return new Region();
        }

        @Override
        public Region fromDTO(RegionDTO dto) throws SQLException {
            Region region = super.fromDTO(dto);

            region.setFlag(new RegionFlag(dto.flag));
            region.setMinRank(dto.minRank);

            return region;
        }

        @Override
        public RegionDTO toDTO(Region entity) throws SQLException {
            RegionDTO dto = super.toDTO(entity);

            dto.flag = entity.flags().get();
            dto.minRank = entity.getMinRank();

            return dto;
        }
    }
    //======================================================
    //========================_LOADER=======================
    //======================================================
}
