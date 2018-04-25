package com.github.manolo8.simplecraft.modules.shop;

import com.github.manolo8.simplecraft.modules.shop.data.ShopRepository;

public class ShopService {

    private final ShopRepository shopRepository;

    public ShopService(ShopRepository shopRepository) {
        this.shopRepository = shopRepository;
    }

    public Shop findOne(int x, int y, int z, int worldId) {
        return shopRepository.findOne(x, y, z, worldId);
    }

    public Shop create(int x, int y, int z, int worldId) {
        return shopRepository.create(x, y, z, worldId);
    }

    public void remove(int x, int y, int z, int worldId) {
        shopRepository.remove(x, y, z, worldId);
    }
}
