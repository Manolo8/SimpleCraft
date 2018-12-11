package com.github.manolo8.simplecraft.module.mobarea.mobs.item;

import com.github.manolo8.simplecraft.core.data.cache.SaveCache;
import com.github.manolo8.simplecraft.core.data.connection.Database;
import com.github.manolo8.simplecraft.core.data.model.base.BaseDAO;
import com.github.manolo8.simplecraft.core.data.model.base.BaseLoader;
import com.github.manolo8.simplecraft.core.data.model.base.BaseRepository;
import com.github.manolo8.simplecraft.core.data.model.base.DTO;
import com.github.manolo8.simplecraft.core.data.table.OnlyInsert;
import com.github.manolo8.simplecraft.module.mobarea.mobs.MobInfo;
import com.github.manolo8.simplecraft.tools.item.ItemRepository;
import org.bukkit.inventory.ItemStack;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class MobDropRepository extends BaseRepository<MobDrop,
        MobDropRepository.KitItemDTO,
        MobDropRepository.KitItemDAO,
        MobDropRepository.KitItemCache,
        MobDropRepository.KitItemLoader> {

    private final ItemRepository itemRepository;

    //======================================================
    //======================REPOSITORY======================
    //======================================================
    public MobDropRepository(Database database, ItemRepository itemRepository) {
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

    public List<MobDrop> findByMobId(int mobId) throws SQLException {
        return findByIdIn(dao.findByMobId(mobId));
    }

    public MobDrop create(MobInfo mob, ItemStack item, double chance) throws SQLException {
        KitItemDTO dto = new KitItemDTO();

        dto.mobId = mob.getId();
        dto.amount = item.getAmount();
        dto.itemId = itemRepository.findOrCreateId(item);
        dto.chance = chance;

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
        private int mobId;
        @OnlyInsert
        private int itemId;
        private int amount;
        private double chance;

    }
    //======================================================
    //=========================_DTO=========================
    //======================================================


    //======================================================
    //==========================DAO=========================
    //======================================================
    class KitItemDAO extends BaseDAO<KitItemDTO> {

        private final String findByMobIdQuery = "SELECT id FROM MobAreaInfoDrops WHERE mobId=?";

        KitItemDAO(Database database) throws SQLException {
            super(database, "MobAreaInfoDrops", KitItemDTO.class);
        }

        private List<Integer> findByMobId(int mobId) throws SQLException {
            PreparedStatement statement = prepareStatement(findByMobIdQuery);

            statement.setInt(1, mobId);

            return fromResultListId(statement);
        }
    }
    //======================================================
    //=========================_DAO=========================
    //======================================================

    //======================================================
    //==========================CACHE=======================
    //======================================================
    class KitItemCache extends SaveCache<MobDrop, MobDropRepository> {

        KitItemCache(MobDropRepository repository) {
            super(repository);
        }
    }
    //======================================================
    //=========================_CACHE=======================
    //======================================================


    //======================================================
    //=========================LOADER=======================
    //======================================================
    class KitItemLoader extends BaseLoader<MobDrop, KitItemDTO> {

        @Override
        public MobDrop newEntity() {
            return new MobDrop();
        }

        @Override
        public MobDrop fromDTO(KitItemDTO dto) throws SQLException {
            MobDrop entity = super.fromDTO(dto);

            ItemStack item = itemRepository.findOne(dto.itemId).get().clone();
            item.setAmount(dto.amount);
            entity.setChance(dto.chance);
            entity.setItem(item);


            return entity;
        }

        @Override
        public KitItemDTO toDTO(MobDrop entity) throws SQLException {
            KitItemDTO dto = super.toDTO(entity);

            dto.amount = entity.getItem().getAmount();
            dto.chance = entity.getChance();

            return dto;
        }
    }
    //======================================================
    //========================_LOADER=======================
    //======================================================
}
