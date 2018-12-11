package com.github.manolo8.simplecraft.module.market;

import com.github.manolo8.simplecraft.core.data.cache.SaveCache;
import com.github.manolo8.simplecraft.core.data.connection.Database;
import com.github.manolo8.simplecraft.core.data.model.base.BaseDAO;
import com.github.manolo8.simplecraft.core.data.model.base.BaseLoader;
import com.github.manolo8.simplecraft.core.data.model.base.BaseRepository;
import com.github.manolo8.simplecraft.core.data.model.base.DTO;
import com.github.manolo8.simplecraft.core.data.table.OnlyInsert;
import com.github.manolo8.simplecraft.module.money.MoneyRepository;
import com.github.manolo8.simplecraft.module.user.User;
import com.github.manolo8.simplecraft.module.user.identity.Identity;
import com.github.manolo8.simplecraft.tools.item.ItemRepository;
import org.bukkit.inventory.ItemStack;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class MarketItemRepository extends BaseRepository<MarketItem,
        MarketItemRepository.MarketItemDTO,
        MarketItemRepository.MarketItemDAO,
        MarketItemRepository.MarketItemCache,
        MarketItemRepository.MarketItemLoader> {

    public static long EXPIRATION_TIME = 1000 * 60 * 60 * 48; //48 horas

    //======================================================
    //======================REPOSITORY======================
    //======================================================

    private final ItemRepository itemRepository;
    private final MoneyRepository moneyRepository;

    public MarketItemRepository(Database database, ItemRepository itemRepository, MoneyRepository moneyRepository) {
        super(database);

        this.itemRepository = itemRepository;
        this.moneyRepository = moneyRepository;
    }

    @Override
    protected MarketItemDAO initDao() throws SQLException {
        return new MarketItemDAO(database);
    }

    @Override
    protected MarketItemLoader initLoader() {
        return new MarketItemLoader();
    }

    @Override
    protected MarketItemCache initCache() {
        return new MarketItemCache(this);
    }

    public List<MarketItem> findByIdentity(Identity identity, int page) throws SQLException {
        return findByIdIn(dao.findByIdentityId(identity.getId(), page, 36));
    }

    public int countByIdentity(Identity identity) throws SQLException {
        return dao.countByIdentityId(identity.getId());
    }

    public List<MarketItem> findByCategory(MarketCategory category, int page) throws SQLException {
        return findByIdIn(dao.findByCategoryAndPage(category.id, page, 36));
    }

    public MarketItem create(User owner, ItemStack itemStack, double cost) throws SQLException {

        MarketItemDTO dto = new MarketItemDTO();

        dto.identityId = owner.identity().getId();
        dto.creation = System.currentTimeMillis();
        dto.cost = cost;
        dto.categoryId = MarketCategory.findByItemStack(itemStack).id;

        dto.quantity = itemStack.getAmount();
        dto.itemId = itemRepository.findOrCreateId(itemStack);

        return create(dto);
    }

    //======================================================
    //=====================_REPOSITORY======================
    //======================================================


    //======================================================
    //==========================DTO=========================
    //======================================================
    class MarketItemDTO extends DTO {

        @OnlyInsert
        private int identityId;
        @OnlyInsert
        private int categoryId;
        @OnlyInsert
        private int itemId;
        @OnlyInsert
        private long creation;
        private int quantity;
        private double cost;

    }
    //======================================================
    //=========================_DTO=========================
    //======================================================


    //======================================================
    //==========================DAO=========================
    //======================================================
    class MarketItemDAO extends BaseDAO<MarketItemDTO> {

        private final String findByIdentityIdQuery = "SELECT id FROM MarketItems WHERE identityId=? ORDER BY creation DESC LIMIT ?,?;";
        private final String findByCategoryAndPage = "SELECT id FROM MarketItems WHERE categoryId=? and creation>? ORDER BY creation DESC LIMIT ?,?;";
        private final String countByIdentityQuery = "SELECT count(id) FROM MarketItems WHERE identityId=?;";

        MarketItemDAO(Database database) throws SQLException {
            super(database, "MarketItems", MarketItemDTO.class);
        }

        public List<Integer> findByIdentityId(int id, int page, int limit) throws SQLException {
            PreparedStatement statement = prepareStatement(findByIdentityIdQuery);

            statement.setInt(1, id);
            statement.setInt(2, limit * page);
            statement.setInt(3, limit);

            return fromResultListId(statement);
        }

        public List<Integer> findByCategoryAndPage(int categoryId, int page, int limit) throws SQLException {
            PreparedStatement statement = prepareStatement(findByCategoryAndPage);

            statement.setInt(1, categoryId);
            statement.setLong(2, System.currentTimeMillis() - EXPIRATION_TIME);
            statement.setInt(3, limit * page);
            statement.setInt(4, limit);

            return fromResultListId(statement);
        }

        public int countByIdentityId(int id) throws SQLException {
            PreparedStatement statement = prepareStatement(countByIdentityQuery);

            statement.setInt(1, id);

            ResultSet result = statement.executeQuery();

            int count = 0;

            if (result.next()) count = result.getInt(1);

            statement.close();

            return count;
        }
    }
    //======================================================
    //=========================_DAO=========================
    //======================================================

    //======================================================
    //==========================CACHE=======================
    //======================================================
    class MarketItemCache extends SaveCache<MarketItem, MarketItemRepository> {

        MarketItemCache(MarketItemRepository repository) {
            super(repository);
        }
    }
    //======================================================
    //=========================_CACHE=======================
    //======================================================


    //======================================================
    //=========================LOADER=======================
    //======================================================
    class MarketItemLoader extends BaseLoader<MarketItem, MarketItemDTO> {

        @Override
        public MarketItem newEntity() {
            return new MarketItem();
        }

        @Override
        public MarketItem fromDTO(MarketItemDTO dto) throws SQLException {
            MarketItem entity = super.fromDTO(dto);

            entity.setCategory(MarketCategory.findById(dto.categoryId));
            entity.setCreation(dto.creation);
            entity.setOwner(moneyRepository.findOneByIdentity(dto.identityId));

            entity.setItem(itemRepository.findOne(dto.itemId).get().clone());
            entity.getItem().setAmount(dto.quantity);
            entity.setCost(dto.cost);

            return entity;
        }

        @Override
        public MarketItemDTO toDTO(MarketItem entity) throws SQLException {
            MarketItemDTO dto = super.toDTO(entity);

            dto.identityId = entity.getOwner().getIdentity().getId();
            dto.quantity = entity.getItem().getAmount();
            dto.categoryId = entity.getCategory().id;
            dto.cost = entity.getCost();
            dto.creation = entity.getCreation();

            return dto;
        }
    }
    //======================================================
    //========================_LOADER=======================
    //======================================================
}
