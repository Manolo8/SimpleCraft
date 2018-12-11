package com.github.manolo8.simplecraft.module.clan.clanarea;

import com.github.manolo8.simplecraft.core.world.WorldInfoRepository;
import com.github.manolo8.simplecraft.core.data.connection.Database;
import com.github.manolo8.simplecraft.core.world.model.container.ContainerDAO;
import com.github.manolo8.simplecraft.core.world.model.container.ContainerDTO;
import com.github.manolo8.simplecraft.core.world.model.container.ContainerLoader;
import com.github.manolo8.simplecraft.core.world.model.container.ContainerRepository;
import com.github.manolo8.simplecraft.core.data.model.named.NamedCache;
import com.github.manolo8.simplecraft.module.clan.ClanRepository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class ClanAreaRepository extends ContainerRepository<ClanArea,
        ClanAreaRepository.ClanAreaDTO,
        ClanAreaRepository.ClanAreaDAO,
        ClanAreaRepository.ClanAreaCache,
        ClanAreaRepository.ClanAreaLoader> {

    private final WorldInfoRepository worldInfoRepository;
    private final ClanRepository clanRepository;

    //======================================================
    //======================REPOSITORY======================
    //======================================================
    public ClanAreaRepository(Database database,
                              WorldInfoRepository worldInfoRepository,
                              ClanRepository clanRepository) {
        super(database);

        this.worldInfoRepository = worldInfoRepository;
        this.clanRepository = clanRepository;
    }

    @Override
    protected ClanAreaDAO initDao() throws SQLException {
        return new ClanAreaDAO(database);
    }

    @Override
    protected ClanAreaLoader initLoader() {
        return new ClanAreaLoader(worldInfoRepository);
    }

    @Override
    protected ClanAreaCache initCache() {
        return new ClanAreaCache(this);
    }

    public List<ClanArea> findByClanId(int clanId) throws SQLException {
        return findByIdIn(dao.findByClanId(clanId));
    }
    //======================================================
    //=====================_REPOSITORY======================
    //======================================================


    //======================================================
    //==========================DTO=========================
    //======================================================
    class ClanAreaDTO extends ContainerDTO {

        private int clanId;
        private long lastConquest;

    }
    //======================================================
    //=========================_DTO=========================
    //======================================================


    //======================================================
    //==========================DAO=========================
    //======================================================
    class ClanAreaDAO extends ContainerDAO<ClanAreaDTO> {

        private final String findByClanIdQuery = "SELECT id FROM ClanAreas WHERE clanId=?";

        ClanAreaDAO(Database database) throws SQLException {
            super(database, "ClanAreas", ClanAreaDTO.class);
        }

        public List<Integer> findByClanId(int clanId) throws SQLException {
            PreparedStatement statement = prepareStatement(findByClanIdQuery);

            statement.setInt(1, clanId);

            return fromResultListId(statement);
        }
    }
    //======================================================
    //=========================_DAO=========================
    //======================================================

    //======================================================
    //==========================CACHE=======================
    //======================================================
    class ClanAreaCache extends NamedCache<ClanArea, ClanAreaRepository> {

        ClanAreaCache(ClanAreaRepository repository) {
            super(repository);
        }
    }
    //======================================================
    //=========================_CACHE=======================
    //======================================================


    //======================================================
    //=========================LOADER=======================
    //======================================================
    class ClanAreaLoader extends ContainerLoader<ClanArea, ClanAreaDTO> {

        protected ClanAreaLoader(WorldInfoRepository worldInfoRepository) {
            super(worldInfoRepository);
        }

        @Override
        public ClanArea newEntity() {
            return new ClanArea();
        }

        @Override
        public ClanArea fromDTO(ClanAreaDTO dto) throws SQLException {
            ClanArea entity = super.fromDTO(dto);

            entity.clan = clanRepository.findOne(dto.clanId);
            entity.lastConquest = dto.lastConquest;

            return entity;
        }

        @Override
        public ClanAreaDTO toDTO(ClanArea entity) throws SQLException {
            ClanAreaDTO dto = super.toDTO(entity);

            dto.clanId = entity.clan == null ? 0 : entity.clan.getId();
            dto.lastConquest = entity.lastConquest;

            return dto;
        }
    }
    //======================================================
    //========================_LOADER=======================
    //======================================================
}
