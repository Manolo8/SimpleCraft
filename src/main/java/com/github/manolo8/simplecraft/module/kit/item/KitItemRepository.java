package com.github.manolo8.simplecraft.module.kit.item;

import com.github.manolo8.simplecraft.core.data.model.base.BaseDAO;
import com.github.manolo8.simplecraft.core.data.model.base.BaseLoader;
import com.github.manolo8.simplecraft.core.data.model.base.BaseRepository;
import com.github.manolo8.simplecraft.core.data.model.base.DTO;
import com.github.manolo8.simplecraft.core.data.cache.SaveCache;
import com.github.manolo8.simplecraft.core.data.connection.Database;
import com.github.manolo8.simplecraft.core.data.table.OnlyInsert;
import com.github.manolo8.simplecraft.module.kit.Kit;
import com.github.manolo8.simplecraft.tools.item.ItemRepository;
import org.bukkit.inventory.ItemStack;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class KitItemRepository extends BaseRepository<KitItem,
        KitItemRepository.KitItemDTO,
        KitItemRepository.KitItemDAO,
        KitItemRepository.KitItemCache,
        KitItemRepository.KitItemLoader> {

    private final ItemRepository itemRepository;

    //======================================================
    //======================REPOSITORY======================
    //======================================================
    public KitItemRepository(Database database, ItemRepository itemRepository) {
        super(database);

        this.itemRepository = itemRepository;
    }

    @Override
    protected KitItemDAO initDao() throws SQLException {
        return new KitItemDAO(database);
    }

    @Override
    protected KitItemLoader initLoader() {
        return new KitItemLoader();
    }

    @Override
    protected KitItemCache initCache() {
        return new KitItemCache(this);
    }

    public List<KitItem> findByKitId(int kitId) throws SQLException {
        return findByIdIn(dao.findByKitId(kitId));
    }

    public KitItem create(ItemStack item, Kit kit) throws SQLException {
        KitItemDTO dto = new KitItemDTO();

        dto.kitId = kit.getId();
        dto.amount = item.getAmount();
        dto.itemId = itemRepository.findOrCreateId(item);

        return create(dto);
    }
    //======================================================
    //=====================_REPOSITORY======================
    //======================================================


    //======================================================
    //==========================DTO=========================
    //======================================================
    class KitItemDTO extends DTO {

        @OnlyInsert
        private int kitId;
        @OnlyInsert
        private int itemId;

        private int amount;
    }
    //======================================================
    //=========================_DTO=========================
    //======================================================


    //======================================================
    //==========================DAO=========================
    //======================================================
    class KitItemDAO extends BaseDAO<KitItemDTO> {

        private final String findByKitIdQuery = "SELECT id FROM KitItems WHERE kitId=?";

        KitItemDAO(Database database) throws SQLException {
            super(database, "KitItems", KitItemDTO.class);
        }

        private List<Integer> findByKitId(int kitId) throws SQLException {
            PreparedStatement statement = prepareStatement(findByKitIdQuery);

            statement.setInt(1, kitId);

            return fromResultListId(statement);
        }
    }
    //======================================================
    //=========================_DAO=========================
    //======================================================

    //======================================================
    //==========================CACHE=======================
    //======================================================
    class KitItemCache extends SaveCache<KitItem, KitItemRepository> {

        KitItemCache(KitItemRepository repository) {
            super(repository);
        }

    }
    //======================================================
    //=========================_CACHE=======================
    //======================================================


    //======================================================
    //=========================LOADER=======================
    //======================================================
    class KitItemLoader extends BaseLoader<KitItem, KitItemDTO> {

        @Override
        public KitItem newEntity() {
            return new KitItem();
        }

        @Override
        public KitItem fromDTO(KitItemDTO dto) throws SQLException {
            KitItem entity = super.fromDTO(dto);

            ItemStack item = itemRepository.findOne(dto.itemId).copy();
            item.setAmount(dto.amount);

            entity.setItem(item);


            return entity;
        }

        @Override
        public KitItemDTO toDTO(KitItem entity) throws SQLException {
            KitItemDTO dto = super.toDTO(entity);

            dto.amount = entity.getItem().getAmount();

            return dto;
        }
    }
    //======================================================
    //========================_LOADER=======================
    //======================================================
}
