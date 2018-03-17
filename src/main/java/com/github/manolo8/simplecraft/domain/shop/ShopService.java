package com.github.manolo8.simplecraft.domain.shop;

import com.github.manolo8.simplecraft.domain.shop.data.ShopDao;
import com.github.manolo8.simplecraft.domain.shop.data.ShopRepository;

import java.util.ArrayList;
import java.util.List;

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
