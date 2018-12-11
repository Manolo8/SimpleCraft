package com.github.manolo8.simplecraft.module.kit;

import com.github.manolo8.simplecraft.core.data.connection.Database;
import com.github.manolo8.simplecraft.core.data.model.named.*;
import com.github.manolo8.simplecraft.module.kit.item.KitItemRepository;
import com.github.manolo8.simplecraft.module.kit.user.KitUserRepository;
import com.github.manolo8.simplecraft.tools.item.ItemRepository;
import com.github.manolo8.simplecraft.module.user.identity.IdentityRepository;

import java.sql.SQLException;

public class KitRepository extends NamedRepository<Kit,
        KitRepository.KitDTO,
        KitRepository.KitDAO,
        KitRepository.KitCache,
        KitRepository.KitLoader> {

    private final KitItemRepository kitItemRepository;
    private final KitUserRepository kitUserRepository;

    //======================================================
    //======================REPOSITORY======================
    //======================================================
    public KitRepository(Database database,
                         IdentityRepository identityRepository,
                         ItemRepository itemRepository) throws SQLException {
        super(database);

        this.kitItemRepository = new KitItemRepository(database, itemRepository);
        this.kitUserRepository = new KitUserRepository(database, identityRepository, this);
    }

    @Override
    public void init() throws SQLException {
        super.init();
        kitUserRepository.init();
        kitItemRepository.init();
    }

    @Override
    protected KitDAO initDao() throws SQLException {
        return new KitDAO(database);
    }

    @Override
    protected KitLoader initLoader() {
        return new KitLoader();
    }

    @Override
    protected KitCache initCache() {
        return new KitCache(this);
    }

    public KitItemRepository getKitItemRepository() {
        return kitItemRepository;
    }

    public KitUserRepository getKitUserRepository() {
        return kitUserRepository;
    }
    //======================================================
    //=====================_REPOSITORY======================
    //======================================================


    //======================================================
    //==========================DTO=========================
    //======================================================
    class KitDTO extends NamedDTO {

        private long delay;
        private int slot;
        private int rank;
    }
    //======================================================
    //=========================_DTO=========================
    //======================================================


    //======================================================
    //==========================DAO=========================
    //======================================================
    class KitDAO extends NamedDAO<KitDTO> {

        KitDAO(Database database) throws SQLException {
            super(database, "Kits", KitDTO.class);
        }
    }
    //======================================================
    //=========================_DAO=========================
    //======================================================

    //======================================================
    //==========================CACHE=======================
    //======================================================
    class KitCache extends NamedCache<Kit, KitRepository> {

        KitCache(KitRepository repository) {
            super(repository);
        }
    }
    //======================================================
    //=========================_CACHE=======================
    //======================================================


    //======================================================
    //=========================LOADER=======================
    //======================================================
    class KitLoader extends NamedLoader<Kit, KitDTO> {

        @Override
        public Kit newEntity() {
            return new Kit(kitItemRepository);
        }

        @Override
        public Kit fromDTO(KitDTO dto) throws SQLException {
            Kit entity = super.fromDTO(dto);

            entity.setDelay(dto.delay);
            entity.setSlot(dto.slot);
            entity.setItems(kitItemRepository.findByKitId(dto.id));
            entity.setRank(dto.rank);

            return entity;
        }

        @Override
        public KitDTO toDTO(Kit entity) throws SQLException {
            KitDTO dto = super.toDTO(entity);

            dto.delay = entity.getDelay();
            dto.slot = entity.getSlot();
            dto.rank = entity.getRank();

            return dto;
        }
    }
    //======================================================
    //========================_LOADER=======================
    //======================================================
}
