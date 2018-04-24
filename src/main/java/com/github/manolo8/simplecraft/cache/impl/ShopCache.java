package com.github.manolo8.simplecraft.cache.impl;

import com.github.manolo8.simplecraft.cache.LocationCache;
import com.github.manolo8.simplecraft.cache.SaveCache;
import com.github.manolo8.simplecraft.modules.shop.Shop;
import com.github.manolo8.simplecraft.modules.shop.data.ShopDao;

public class ShopCache extends LocationCache<Shop> implements SaveCache<Shop> {

    private final ShopDao shopDao;

    public ShopCache(ShopDao shopDao) {
        super(Shop.class);
        this.shopDao = shopDao;
    }

    @Override
    public void save(Shop shop) {
        shopDao.save(shop);
    }
}
