package com.github.manolo8.simplecraft.module.kit.user;

import com.github.manolo8.simplecraft.core.data.connection.Database;
import com.github.manolo8.simplecraft.module.user.model.identity.*;
import com.github.manolo8.simplecraft.module.kit.KitRepository;
import com.github.manolo8.simplecraft.module.kit.user.delay.KitDelayRepository;
import com.github.manolo8.simplecraft.module.user.identity.IdentityRepository;
import com.github.manolo8.simplecraft.utils.entity.LazyLoaderList;

import java.sql.SQLException;

public class KitUserRepository extends BaseIdentityRepository<KitUser,
        KitUserRepository.KitUserDTO,
        KitUserRepository.KitUserDAO,
        KitUserRepository.KitUserCache,
        KitUserRepository.KitUserLoader> {

    private final KitDelayRepository kitDelayRepository;

    //======================================================
    //======================REPOSITORY======================
    //======================================================

    public KitUserRepository(Database database,
                             IdentityRepository identityRepository,
                             KitRepository kitRepository) throws SQLException {
        super(database, identityRepository);

        this.kitDelayRepository = new KitDelayRepository(database, identityRepository, kitRepository);
    }

    @Override
    public void init() throws SQLException {
        super.init();

        kitDelayRepository.init();
    }

    @Override
    protected KitUserDAO initDao() throws SQLException {
        return new KitUserDAO(database);
    }

    @Override
    protected KitUserLoader initLoader() {
        return new KitUserLoader(identityRepository);
    }

    @Override
    protected KitUserCache initCache() {
        return new KitUserCache(this);
    }

    public KitDelayRepository getKitDelayRepository() {
        return kitDelayRepository;
    }
    //======================================================
    //=====================_REPOSITORY======================
    //======================================================


    //======================================================
    //==========================DTO=========================
    //======================================================
    class KitUserDTO extends BaseIdentityDTO {
    }
    //======================================================
    //=========================_DTO=========================
    //======================================================


    //======================================================
    //==========================DAO=========================
    //======================================================
    class KitUserDAO extends BaseIdentityDAO<KitUserDTO> {

        KitUserDAO(Database database) throws SQLException {
            super(database, "KitUsers", KitUserDTO.class);
        }

    }
    //======================================================
    //=========================_DAO=========================
    //======================================================

    //======================================================
    //==========================CACHE=======================
    //======================================================
    class KitUserCache extends BaseIdentityCache<KitUser, KitUserRepository> {

        KitUserCache(KitUserRepository repository) {
            super(repository);
        }
    }
    //======================================================
    //=========================_CACHE=======================
    //======================================================


    //======================================================
    //=========================LOADER=======================
    //======================================================
    class KitUserLoader extends BaseIdentityLoader<KitUser, KitUserDTO> {

        public KitUserLoader(IdentityRepository identityRepository) {
            super(identityRepository);
        }

        @Override
        public KitUser newEntity() {
            return new KitUser(kitDelayRepository);
        }

        @Override
        public KitUser fromDTO(KitUserDTO dto) throws SQLException {
            KitUser entity = super.fromDTO(dto);

            entity.setUsedKits(new LazyLoaderList<>(() -> kitDelayRepository.findByOwnerId(dto.getIdentityId())));

            return entity;
        }
    }
    //======================================================
    //========================_LOADER=======================
    //======================================================
}
