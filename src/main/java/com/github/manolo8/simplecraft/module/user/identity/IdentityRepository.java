package com.github.manolo8.simplecraft.module.user.identity;

import com.github.manolo8.simplecraft.core.data.cache.Cache;
import com.github.manolo8.simplecraft.core.data.connection.Database;
import com.github.manolo8.simplecraft.core.data.model.named.NamedCache;
import com.github.manolo8.simplecraft.core.data.model.named.NamedDAO;
import com.github.manolo8.simplecraft.core.data.model.named.NamedLoader;
import com.github.manolo8.simplecraft.core.data.model.named.NamedRepository;
import com.github.manolo8.simplecraft.module.skin.SkinRepository;
import com.github.manolo8.simplecraft.module.user.UserRepository;

import java.lang.ref.Reference;
import java.sql.SQLException;
import java.util.UUID;

public class IdentityRepository extends NamedRepository<Identity,
        IdentityDTO,
        IdentityRepository.IdentityDAO,
        IdentityRepository.IdentityCache,
        IdentityRepository.IdentityLoader> {

    //======================================================
    //======================REPOSITORY======================
    //======================================================
    private final SkinRepository skinRepository;

    public IdentityRepository(Database database, SkinRepository skinRepository) {
        super(database);

        this.skinRepository = skinRepository;
    }

    @Override
    protected IdentityDAO initDao() throws SQLException {
        return new IdentityDAO(database);
    }

    @Override
    protected IdentityLoader initLoader() {
        return new IdentityLoader();
    }

    @Override
    protected IdentityCache initCache() {
        return new IdentityCache(this);
    }

    public Identity findByUser(UserRepository.UserDTO userDTO) throws SQLException {
        synchronized (Cache.LOCKER) {
            Identity identity = cache.getIfMatchId(userDTO.id);

            return identity == null ? fromDTO(userDTO) : identity;
        }
    }

    //======================================================
    //=====================_REPOSITORY======================
    //======================================================


    //======================================================
    //==========================DAO=========================
    //======================================================
    class IdentityDAO extends NamedDAO<IdentityDTO> {

        protected IdentityDAO(Database database) throws SQLException {
            super(database, "Users", IdentityDTO.class);
        }

    }
    //======================================================
    //=========================_DAO=========================
    //======================================================


    //======================================================
    //=========================CACHE========================
    //======================================================
    public class IdentityCache extends NamedCache<Identity, IdentityRepository> {

        public IdentityCache(IdentityRepository repository) {
            super(repository);
        }

        //======================================================
        //========================SEARCH========================
        //======================================================
        public Identity getIfMathUUID(UUID uuid) {
            for (Reference<Identity> reference : references) {

                Identity identity = extract(reference);

                if (identity != null && identity.getUuid().equals(uuid)) {
                    return identity;
                }

            }

            return null;
        }
        //======================================================
        //=======================_SEARCH========================
        //======================================================
    }
    //======================================================
    //========================_CACHE========================
    //======================================================


    //======================================================
    //========================LOADER========================
    //======================================================
    protected class IdentityLoader extends NamedLoader<Identity, IdentityDTO> {

        @Override
        public Identity newEntity() {
            return new Identity();
        }

        @Override
        public Identity fromDTO(IdentityDTO dto) throws SQLException {
            Identity identity = super.fromDTO(dto);

            identity.setUuid(new UUID(dto.mostSigBits, dto.leastSigBits));
            identity.setBan(dto.ban);
            identity.setMute(dto.mute);
            identity.setOnlineAllTime(dto.onlineAllTime);
            identity.setFirstLogin(dto.firstLogin);
            identity.setLastLogin(dto.lastLogin);
            identity.setSkin(skinRepository.findOne(dto.skinId));

            return identity;
        }

        @Override
        public IdentityDTO toDTO(Identity entity) throws SQLException {
            IdentityDTO dto = super.toDTO(entity);

            dto.mostSigBits = entity.getUuid().getMostSignificantBits();
            dto.leastSigBits = entity.getUuid().getLeastSignificantBits();
            dto.mute = entity.getMute();
            dto.ban = entity.getBan();
            dto.onlineAllTime = entity.getOnlineAllTime();
            dto.firstLogin = entity.getFirstLogin();
            dto.lastLogin = entity.getLastLogin();
            dto.skinId = (entity.getSkin() == null ? 0 : entity.getSkin().getId());

            return dto;
        }
    }
    //======================================================
    //=======================_LOADER========================
    //======================================================
}
