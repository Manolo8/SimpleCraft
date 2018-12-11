package com.github.manolo8.simplecraft.module.clan;

import com.github.manolo8.simplecraft.core.data.cache.Cache;
import com.github.manolo8.simplecraft.core.data.connection.Database;
import com.github.manolo8.simplecraft.core.data.model.named.*;
import com.github.manolo8.simplecraft.core.data.table.OnlyInsert;
import com.github.manolo8.simplecraft.core.world.WorldInfoRepository;
import com.github.manolo8.simplecraft.module.clan.clanarea.ClanAreaRepository;
import com.github.manolo8.simplecraft.module.clan.invite.ClanInviteRepository;
import com.github.manolo8.simplecraft.module.clan.user.ClanUserRepository;
import com.github.manolo8.simplecraft.module.user.identity.IdentityRepository;
import com.github.manolo8.simplecraft.utils.entity.LazyLoaderList;

import java.lang.ref.Reference;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ClanRepository extends NamedRepository<Clan,
        ClanRepository.ClanDTO,
        ClanRepository.ClanDAO,
        ClanRepository.ClanCache,
        ClanRepository.ClanLoader> {

    private final ClanUserRepository clanUserRepository;
    private final ClanInviteRepository clanInviteRepository;
    private final ClanAreaRepository clanAreaRepository;

    //======================================================
    //======================REPOSITORY======================
    //======================================================
    public ClanRepository(Database database,
                          IdentityRepository identityRepository,
                          WorldInfoRepository worldInfoRepository) throws SQLException {
        super(database);

        this.clanUserRepository = new ClanUserRepository(database, identityRepository, this);
        this.clanInviteRepository = new ClanInviteRepository(database, identityRepository, this);
        this.clanAreaRepository = new ClanAreaRepository(database, worldInfoRepository, this);
    }

    @Override
    public void init() throws SQLException {
        super.init();

        this.clanUserRepository.init();
        this.clanInviteRepository.init();
        this.clanAreaRepository.init();
    }

    @Override
    protected ClanDAO initDao() throws SQLException {
        return new ClanDAO(database);
    }

    @Override
    protected ClanLoader initLoader() {
        return new ClanLoader();
    }

    @Override
    protected ClanCache initCache() {
        return new ClanCache(this);
    }

    public Clan create(String name, String tag, String tagColored) throws SQLException {
        ClanDTO dto = new ClanDTO();

        dto.name = name;
        dto.fastName = name.toLowerCase();

        dto.tag = tag.toLowerCase();
        dto.coloredTag = tagColored;

        dto.friendlyFire = false;
        dto.founded = System.currentTimeMillis();

        return create(dto);
    }

    public Clan findByTag(String tag) throws SQLException {
        synchronized (Cache.LOCKER) {
            tag = tag.toLowerCase();

            Clan clan = cache.getIfMatchTag(tag);

            if (clan != null) {
                clan = fromDTO(dao.findByTag(tag));
            }

            return clan;
        }
    }

    public List<String> findTags(String tag) throws SQLException {
        List<String> tags;

        synchronized (Cache.LOCKER) {
            tags = cache.getIfStartWithTag(tag);
        }

        if (tags.size() < 10) {
            List<String> query = dao.findTagList(tag, 10 - tags.size());

            for (String string : query)
                if (!tags.contains(string))
                    tags.add(string);

        }

        return tags;
    }

    public ClanUserRepository getClanUserRepository() {
        return clanUserRepository;
    }

    public ClanInviteRepository getClanInviteRepository() {
        return clanInviteRepository;
    }

    public ClanAreaRepository getClanAreaRepository() {
        return clanAreaRepository;
    }
    //======================================================
    //=====================_REPOSITORY======================
    //======================================================


    //======================================================
    //==========================DTO=========================
    //======================================================
    class ClanDTO extends NamedDTO {

        @OnlyInsert
        private String tag;
        private String coloredTag;
        @OnlyInsert
        private long founded;

        private boolean friendlyFire;

        private int kills;
        private int deaths;
        private int kdr;

    }
    //======================================================
    //=========================_DTO=========================
    //======================================================


    //======================================================
    //==========================DAO=========================
    //======================================================
    class ClanDAO extends NamedDAO<ClanDTO> {

        private final String findByTagQuery = "SELECT * FROM Clans WHERE tag=?";
        private final String findTagListQuery = "SELECT tag FROM Clans WHERE tag like ? LIMIT ?";

        ClanDAO(Database database) throws SQLException {
            super(database, "Clans", ClanDTO.class);
        }

        public ClanDTO findByTag(String tag) throws SQLException {
            PreparedStatement statement = prepareStatement(findByTagQuery);

            statement.setString(1, tag);

            ResultSet result = statement.executeQuery();

            ClanDTO dto = result.next() ? fromResult(result) : null;

            statement.close();

            return dto;
        }

        public List<String> findTagList(String tag, int limit) throws SQLException {
            PreparedStatement statement = prepareStatement(findTagListQuery);

            statement.setString(1, tag + "%");
            statement.setInt(2, limit);

            ResultSet result = statement.executeQuery();

            List<String> names = new ArrayList<>();

            while (result.next()) names.add(result.getString(1));

            statement.close();

            return names;
        }
    }
    //======================================================
    //=========================_DAO=========================
    //======================================================

    //======================================================
    //==========================CACHE=======================
    //======================================================
    class ClanCache extends NamedCache<Clan, ClanRepository> {

        ClanCache(ClanRepository repository) {
            super(repository);
        }

        public Clan getIfMatchTag(String tag) {
            for (Reference<Clan> reference : references) {
                Clan clan = extract(reference);
                if (clan != null && clan.getTag().equals(tag)) {
                    return clan;
                }
            }
            return null;
        }

        public List<String> getIfStartWithTag(String start) {
            List<String> list = new ArrayList<>();

            for (Reference<Clan> reference : references) {
                Clan clan = extract(reference);
                if (clan != null && clan.getTag().startsWith(start)) {
                    list.add(clan.getTag());
                }
            }

            return list;
        }
    }
    //======================================================
    //=========================_CACHE=======================
    //======================================================


    //======================================================
    //=========================LOADER=======================
    //======================================================
    class ClanLoader extends NamedLoader<Clan, ClanDTO> {

        @Override
        public Clan newEntity() {
            return new Clan(clanInviteRepository);
        }

        @Override
        public Clan fromDTO(ClanDTO dto) throws SQLException {
            Clan entity = super.fromDTO(dto);

            entity.founded = dto.founded;
            entity.friendlyFire = dto.friendlyFire;
            entity.tag = dto.tag;
            entity.coloredTag = dto.coloredTag;

            entity.kills = dto.kills;
            entity.deaths = dto.deaths;

            entity.members = new LazyLoaderList<>(() -> clanUserRepository.findClanUsers(dto.id));
            entity.invites = new LazyLoaderList<>(() -> clanInviteRepository.findByClanId(dto.id));
            entity.areas = new LazyLoaderList<>(() -> clanAreaRepository.findByClanId(dto.id));

            return entity;
        }

        @Override
        public ClanDTO toDTO(Clan entity) throws SQLException {
            ClanDTO dto = super.toDTO(entity);

            dto.coloredTag = entity.coloredTag;
            dto.friendlyFire = entity.friendlyFire;
            dto.kills = entity.kills;
            dto.deaths = entity.deaths;
            dto.kdr = entity.getKdr();

            return dto;
        }
    }
    //======================================================
    //========================_LOADER=======================
    //======================================================
}
