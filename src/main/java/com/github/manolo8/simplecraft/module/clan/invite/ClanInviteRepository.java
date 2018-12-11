package com.github.manolo8.simplecraft.module.clan.invite;

import com.github.manolo8.simplecraft.core.data.cache.SaveCache;
import com.github.manolo8.simplecraft.core.data.connection.Database;
import com.github.manolo8.simplecraft.core.data.model.base.BaseDAO;
import com.github.manolo8.simplecraft.core.data.model.base.BaseLoader;
import com.github.manolo8.simplecraft.core.data.model.base.BaseRepository;
import com.github.manolo8.simplecraft.core.data.model.base.DTO;
import com.github.manolo8.simplecraft.module.clan.Clan;
import com.github.manolo8.simplecraft.module.clan.ClanRepository;
import com.github.manolo8.simplecraft.module.user.identity.Identity;
import com.github.manolo8.simplecraft.module.user.identity.IdentityRepository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class ClanInviteRepository extends BaseRepository<ClanInvite,
        ClanInviteRepository.ClanInviteDTO,
        ClanInviteRepository.ClanInviteDAO,
        ClanInviteRepository.ClanInviteCache,
        ClanInviteRepository.ClanInviteLoader> {

    private final ClanRepository clanRepository;
    private final IdentityRepository identityRepository;

    //======================================================
    //======================REPOSITORY======================
    //======================================================
    public ClanInviteRepository(Database database,
                                IdentityRepository identityRepository,
                                ClanRepository clanRepository) {
        super(database);

        this.identityRepository = identityRepository;
        this.clanRepository = clanRepository;
    }

    @Override
    protected ClanInviteDAO initDao() throws SQLException {
        return new ClanInviteDAO(database);
    }

    @Override
    protected ClanInviteLoader initLoader() {
        return new ClanInviteLoader();
    }

    @Override
    protected ClanInviteCache initCache() {
        return new ClanInviteCache(this);
    }

    public ClanInvite create(Identity invited, Clan clan) throws SQLException {
        ClanInviteDTO dto = new ClanInviteDTO();

        dto.invitedId = invited.getId();
        dto.clanId = clan.getId();

        return create(dto);
    }

    public List<ClanInvite> findByClanId(int clanId) throws SQLException {
        return findByIdIn(dao.findByClanId(clanId));
    }

    public List<ClanInvite> findByIdentity(Identity identity) throws SQLException {
        return findByIdIn(dao.findByIdentityId(identity.getId()));
    }
    //======================================================
    //=====================_REPOSITORY======================
    //======================================================


    //======================================================
    //==========================DTO=========================
    //======================================================
    class ClanInviteDTO extends DTO {

        private int clanId;
        private int invitedId;

    }
    //======================================================
    //=========================_DTO=========================
    //======================================================


    //======================================================
    //==========================DAO=========================
    //======================================================
    class ClanInviteDAO extends BaseDAO<ClanInviteDTO> {

        private final String findByClanIdQuery = "SELECT id FROM ClanInvites WHERE clanId=?";
        private final String findByIdentityId = "SELECT id FROM ClanInvites WHERE invitedId=?";

        ClanInviteDAO(Database database) throws SQLException {
            super(database, "ClanInvites", ClanInviteDTO.class);
        }

        public List<Integer> findByClanId(int clanId) throws SQLException {
            PreparedStatement statement = prepareStatement(findByClanIdQuery);

            statement.setInt(1, clanId);

            return fromResultListId(statement);
        }

        public List<Integer> findByIdentityId(int identityId) throws SQLException {
            PreparedStatement statement = prepareStatement(findByIdentityId);

            statement.setInt(1, identityId);

            return fromResultListId(statement);
        }
    }
    //======================================================
    //=========================_DAO=========================
    //======================================================

    //======================================================
    //==========================CACHE=======================
    //======================================================
    class ClanInviteCache extends SaveCache<ClanInvite, ClanInviteRepository> {

        ClanInviteCache(ClanInviteRepository repository) {
            super(repository);
        }
    }
    //======================================================
    //=========================_CACHE=======================
    //======================================================


    //======================================================
    //=========================LOADER=======================
    //======================================================
    class ClanInviteLoader extends BaseLoader<ClanInvite, ClanInviteDTO> {

        @Override
        public ClanInvite newEntity() {
            return new ClanInvite();
        }

        @Override
        public ClanInvite fromDTO(ClanInviteDTO dto) throws SQLException {
            ClanInvite entity = super.fromDTO(dto);

            entity.clan = clanRepository.findOne(dto.clanId);
            entity.invited = identityRepository.findOne(dto.invitedId);

            return entity;
        }

        @Override
        public ClanInviteDTO toDTO(ClanInvite entity) throws SQLException {
            ClanInviteDTO dto = super.toDTO(entity);

            dto.clanId = entity.clan.getId();
            dto.invitedId = entity.invited.getId();

            return dto;
        }
    }
    //======================================================
    //========================_LOADER=======================
    //======================================================
}