package com.github.manolo8.simplecraft.modules.shop.data;

import com.github.manolo8.simplecraft.cache.impl.ShopCache;
import com.github.manolo8.simplecraft.modules.shop.Shop;
import com.github.manolo8.simplecraft.modules.user.data.UserRepository;

public class ShopRepository {

    private final UserRepository userRepository;
    private final ShopDao shopDao;
    private final ShopCache shopCache;

    public ShopRepository(UserRepository userRepository, ShopDao shopDao, ShopCache shopCache) {
        this.userRepository = userRepository;
        this.shopDao = shopDao;
        this.shopCache = shopCache;
    }

    public Shop findOne(int x, int y, int z, int worldId) {
        Shop shop = shopCache.getIfMatch(x, y, z, worldId);

        if (shop != null) return shop;

        ShopDTO dto = shopDao.findOne(x, y, z, worldId);

        if (dto == null) return null;

        return fromDTO(dto);
    }

    public Shop create(int x, int y, int z, int worldId) {
        shopDao.create(x, y, z, worldId);
        return findOne(x, y, z, worldId);
    }

    public void remove(int x, int y, int z, int worldId) {
        Shop shop = findOne(x, y, z, worldId);

        if (shop == null) {
            System.out.println("Tentando remover um shopping nulo...");
            return;
        }

        //Remove do cache
        shopCache.remove(shop);
        //deleta do banco de dados
        shopDao.delete(shop);

    }

    private Shop fromDTO(ShopDTO dto) {
        Shop shop = new Shop();

        shop.setOwner(userRepository.findOne(dto.getOwnerId()));
        shop.setId(dto.getId());
        shop.setX(dto.getX());
        shop.setY(dto.getY());
        shop.setZ(dto.getZ());
        shop.setWorldId(dto.getWorldId());
        shop.setTotalBuy(dto.getTotalBuy());
        shop.setTotalSell(dto.getTotalSell());
        shop.setSellPrice(dto.getSellPrice());
        shop.setBuyPrice(dto.getBuyPrice());
        shop.setItemStack(dto.getItemStack());

        shopCache.add(shop);

        return shop;
    }
}
