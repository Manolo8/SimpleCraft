package com.github.manolo8.simplecraft.module.shop;

import com.github.manolo8.simplecraft.core.world.WorldInfoRepository;
import com.github.manolo8.simplecraft.core.world.container.Area;
import com.github.manolo8.simplecraft.core.data.connection.Database;
import com.github.manolo8.simplecraft.core.world.model.container.ContainerDAO;
import com.github.manolo8.simplecraft.core.world.model.container.ContainerDTO;
import com.github.manolo8.simplecraft.core.world.model.container.ContainerLoader;
import com.github.manolo8.simplecraft.core.world.model.container.ContainerRepository;
import com.github.manolo8.simplecraft.core.data.model.named.NamedCache;
import com.github.manolo8.simplecraft.module.money.MoneyRepository;
import com.github.manolo8.simplecraft.tools.item.ItemRepository;

import java.sql.SQLException;

public class ShopRepository extends ContainerRepository<Shop,
        ShopRepository.ShopDTO,
        ShopRepository.ShopDAO,
        ShopRepository.ShopCache,
        ShopRepository.ShopLoader> {

    private final WorldInfoRepository worldInfoRepository;
    private final MoneyRepository moneyRepository;
    private final ItemRepository itemRepository;

    //======================================================
    //======================REPOSITORY======================
    //======================================================
    public ShopRepository(Database database,
                          WorldInfoRepository worldInfoRepository,
                          MoneyRepository moneyRepository,
                          ItemRepository itemRepository) {
        super(database);

        this.worldInfoRepository = worldInfoRepository;
        this.moneyRepository = moneyRepository;
        this.itemRepository = itemRepository;
    }

    @Override
    protected ShopDAO initDao() throws SQLException {
        return new ShopDAO(database);
    }

    @Override
    protected ShopLoader initLoader() {
        return new ShopLoader();
    }

    @Override
    protected ShopCache initCache() {
        return new ShopCache(this);
    }

    public Shop create(ShopConverter converter, Area area) throws SQLException {
        ShopDTO dto = new ShopDTO();

        dto.worldId = converter.worldId;
        dto.buy = converter.buyPrice;
        dto.sell = converter.sellPrice;
        dto.totalSell = 0;
        dto.totalBuy = 0;
        dto.identityId = converter.identityId;
        dto.load(area);

        return create(dto);
    }
    //======================================================
    //=====================_REPOSITORY======================
    //======================================================


    //======================================================
    //==========================DTO=========================
    //======================================================
    static class ShopDTO extends ContainerDTO {

        private int identityId;

        private double buy;
        private double sell;

        private int itemId;

        private int totalBuy;
        private int totalSell;

        private int stock;
    }
    //======================================================
    //==========================DTO=========================
    //======================================================


    //======================================================
    //==========================DAO=========================
    //======================================================
    class ShopDAO extends ContainerDAO<ShopDTO> {

        protected ShopDAO(Database database) throws SQLException {
            super(database, "Shops", ShopDTO.class);
        }

    }
    //======================================================
    //=========================_DAO=========================
    //======================================================


    //======================================================
    //==========================CACHE=======================
    //======================================================
    class ShopCache extends NamedCache<Shop, ShopRepository> {

        public ShopCache(ShopRepository repository) {
            super(repository);
        }
    }
    //======================================================
    //=========================_CACHE=======================
    //======================================================

    //======================================================
    //=========================LOADER=======================
    //======================================================
    class ShopLoader extends ContainerLoader<Shop, ShopDTO> {

        ShopLoader() {
            super(worldInfoRepository);
        }

        @Override
        public Shop newEntity() {
            return new Shop();
        }

        @Override
        public Shop fromDTO(ShopDTO dto) throws SQLException {
            Shop shop = super.fromDTO(dto);

            shop.setMoney(moneyRepository.findOneByIdentity(dto.identityId));
            shop.setBuy(dto.buy);
            shop.setSell(dto.sell);
            shop.setTotalBuy(dto.totalBuy);
            shop.setTotalSell(dto.totalSell);
            shop.setStock(dto.stock);
            shop.setItem(dto.itemId == 0 ? null : itemRepository.findOne(dto.itemId).copy());

            return shop;
        }

        @Override
        public ShopDTO toDTO(Shop entity) throws SQLException {
            ShopDTO dto = super.toDTO(entity);

            dto.identityId = entity.getMoney().getIdentity().getId();
            dto.buy = entity.getBuy();
            dto.sell = entity.getSell();
            dto.totalBuy = entity.getTotalBuy();
            dto.totalSell = entity.getTotalSell();
            dto.stock = entity.getStock();
            dto.itemId = itemRepository.findOrCreateId(entity.getItem());

            return dto;
        }
    }
    //======================================================
    //========================_LOADER=======================
    //======================================================
}
