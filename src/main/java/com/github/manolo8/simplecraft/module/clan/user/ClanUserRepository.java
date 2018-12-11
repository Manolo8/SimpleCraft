package com.github.manolo8.simplecraft.module.clan.user;

import com.github.manolo8.simplecraft.core.data.connection.Database;
import com.github.manolo8.simplecraft.core.data.table.Size;
import com.github.manolo8.simplecraft.module.clan.ClanFlag;
import com.github.manolo8.simplecraft.module.clan.ClanRepository;
import com.github.manolo8.simplecraft.module.user.identity.IdentityRepository;
import com.github.manolo8.simplecraft.module.user.model.identity.*;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class ClanUserRepository extends BaseIdentityRepository<ClanUser,
        ClanUserRepository.ClanUserDTO,
        ClanUserRepository.ClanUserDAO,
        ClanUserRepository.ClanUserCache,
        ClanUserRepository.ClanUserLoader> {

    private final ClanRepository clanRepository;

    //======================================================
    //======================REPOSITORY======================
    //======================================================
    public ClanUserRepository(Database database,
                              IdentityRepository identityRepository,
                              ClanRepository clanRepository) {
        super(database, identityRepository);

        this.clanRepository = clanRepository;
    }

    @Override
    protected ClanUserDAO initDao() throws SQLException {
        return new ClanUserDAO(database);
    }

    @Override
    protected ClanUserLoader initLoader() {
        return new ClanUserLoader(identityRepository);
    }

    @Override
    protected ClanUserCache initCache() {
        return new ClanUserCache(this);
    }

    public List<ClanUser> findClanUsers(int clanId) throws SQLException {
        return super.findByIdIn(dao.findByClanId(clanId));
    }

    //======================================================
    //=====================_REPOSITORY======================
    //======================================================


    //======================================================
    //==========================DTO=========================
    //======================================================
    class ClanUserDTO extends BaseIdentityDTO {

        @Size(2)
        private byte[] flag;
        private int clanId;

        private int kills;
        private int deaths;
        private int killingSpree;
        private int bestKillingSpree;

    }
    //======================================================
    //=========================_DTO=========================
    //======================================================


    //======================================================
    //==========================DAO=========================
    //======================================================
    class ClanUserDAO extends BaseIdentityDAO<ClanUserDTO> {

        private final String findByClanIdQuery = "SELECT id FROM ClanUsers WHERE clanId=?";

        ClanUserDAO(Database database) throws SQLException {
            super(database, "ClanUsers", ClanUserDTO.class);
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
    class ClanUserCache extends BaseIdentityCache<ClanUser, ClanUserRepository> {

        ClanUserCache(ClanUserRepository repository) {
            super(repository);
        }
    }
    //======================================================
    //=========================_CACHE=======================
    //======================================================


    //======================================================
    //=========================LOADER=======================
    //======================================================
    class ClanUserLoader extends BaseIdentityLoader<ClanUser, ClanUserDTO> {

        public ClanUserLoader(IdentityRepository identityRepository) {
            super(identityRepository);
        }

        @Override
        public ClanUser newEntity() {
            return new ClanUser();
        }

        @Override
        public ClanUser fromDTO(ClanUserDTO dto) throws SQLException {
            ClanUser entity = super.fromDTO(dto);

            entity.clan = clanRepository.findOne(dto.clanId);
            entity.flag = new ClanFlag(dto.flag);

            entity.kills = dto.kills;
            entity.deaths = dto.deaths;
            entity.killingSpree = dto.killingSpree;
            entity.bestKillingSpree = dto.bestKillingSpree;

            return entity;
        }

        @Override
        public ClanUserDTO toDTO(ClanUser entity) throws SQLException {
            ClanUserDTO dto = super.toDTO(entity);

            dto.clanId = entity.clan == null ? 0 : entity.clan.getId();
            dto.flag = entity.flag.get();

            dto.kills = entity.kills;
            dto.deaths = entity.deaths;
            dto.killingSpree = entity.killingSpree;
            dto.bestKillingSpree = entity.bestKillingSpree;

            return dto;
        }
    }
    //======================================================
    //========================_LOADER=======================
    //======================================================
}
