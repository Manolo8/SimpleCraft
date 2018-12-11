package com.github.manolo8.simplecraft.module.kit.user.delay;

import com.github.manolo8.simplecraft.core.data.model.base.BaseDAO;
import com.github.manolo8.simplecraft.core.data.model.base.BaseLoader;
import com.github.manolo8.simplecraft.core.data.model.base.BaseRepository;
import com.github.manolo8.simplecraft.core.data.model.base.DTO;
import com.github.manolo8.simplecraft.core.data.cache.SaveCache;
import com.github.manolo8.simplecraft.core.data.connection.Database;
import com.github.manolo8.simplecraft.module.kit.Kit;
import com.github.manolo8.simplecraft.module.kit.KitRepository;
import com.github.manolo8.simplecraft.module.kit.user.KitUser;
import com.github.manolo8.simplecraft.module.user.identity.IdentityRepository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class KitDelayRepository extends BaseRepository<KitDelay,
        KitDelayRepository.KitDelayDTO,
        KitDelayRepository.KitDelayDAO,
        KitDelayRepository.KitDelayCache,
        KitDelayRepository.KitDelayLoader> {

    private final IdentityRepository identityRepository;
    private final KitRepository kitRepository;

    //======================================================
    //======================REPOSITORY======================
    //======================================================
    public KitDelayRepository(Database database,
                              IdentityRepository identityRepository,
                              KitRepository kitRepository) {
        super(database);

        this.identityRepository = identityRepository;
        this.kitRepository = kitRepository;
    }

    @Override
    protected KitDelayDAO initDao() throws SQLException {
        return new KitDelayDAO(database);
    }

    @Override
    protected KitDelayLoader initLoader() {
        return new KitDelayLoader();
    }

    @Override
    protected KitDelayCache initCache() {
        return new KitDelayCache(this);
    }

    public List<KitDelay> findByOwnerId(int ownerId) throws SQLException {
        return findByIdIn(dao.findByOwnerId(ownerId));
    }

    public KitDelay create(Kit kit, KitUser kitUser) throws SQLException {
        KitDelayDTO dto = new KitDelayDTO();

        dto.kitId = kit.getId();
        dto.ownerId = kitUser.getIdentity().getId();
        dto.lastUse = 0;

        return create(dto);
    }
    //======================================================
    //=====================_REPOSITORY======================
    //======================================================


    //======================================================
    //==========================DTO=========================
    //======================================================
    class KitDelayDTO extends DTO {

        private int ownerId;
        private int kitId;
        private long lastUse;
    }
    //======================================================
    //=========================_DTO=========================
    //======================================================


    //======================================================
    //==========================DAO=========================
    //======================================================
    class KitDelayDAO extends BaseDAO<KitDelayDTO> {

        private String findByOwnerIdQuery = "SELECT id FROM KitDelays WHERE ownerId=?";

        KitDelayDAO(Database database) throws SQLException {
            super(database, "KitDelays", KitDelayDTO.class);
        }

        private List<Integer> findByOwnerId(int id) throws SQLException {
            PreparedStatement statement = prepareStatement(findByOwnerIdQuery);

            statement.setInt(1, id);

            return fromResultListId(statement);
        }
    }
    //======================================================
    //=========================_DAO=========================
    //======================================================

    //======================================================
    //==========================CACHE=======================
    //======================================================
    class KitDelayCache extends SaveCache<KitDelay, KitDelayRepository> {

        KitDelayCache(KitDelayRepository repository) {
            super(repository);
        }
    }
    //======================================================
    //=========================_CACHE=======================
    //======================================================


    //======================================================
    //=========================LOADER=======================
    //======================================================
    class KitDelayLoader extends BaseLoader<KitDelay, KitDelayDTO> {

        @Override
        public KitDelay newEntity() {
            return new KitDelay();
        }

        @Override
        public KitDelay fromDTO(KitDelayDTO dto) throws SQLException {
            KitDelay entity = super.fromDTO(dto);

            entity.setKit(kitRepository.findOne(dto.kitId));
            entity.setOwner(identityRepository.findOne(dto.ownerId));
            entity.setLastUse(dto.lastUse);

            return entity;
        }

        @Override
        public KitDelayDTO toDTO(KitDelay entity) throws SQLException {
            KitDelayDTO dto = super.toDTO(entity);

            dto.kitId = entity.getKit().getId();
            dto.ownerId = entity.getOwner().getId();
            dto.lastUse = entity.getLastUse();

            return dto;
        }
    }
    //======================================================
    //========================_LOADER=======================
    //======================================================
}
