package com.github.manolo8.simplecraft.domain.shop.data;

import com.github.manolo8.simplecraft.domain.shop.Shop;

public interface ShopDao {

    /**
     * @param x       pos x
     * @param y       pos y
     * @param z       pos z
     * @param worldId worldId
     * @return Shop or null if does not exists
     */
    ShopDTO findOne(int x, int y, int z, int worldId);

    /**
     * @param x       pos x
     * @param y       pos y
     * @param z       pos z
     * @param worldId worldId
     */
    void create(int x, int y, int z, int worldId);

    void delete(Shop shop);

    /**
     * @param shop save the shop in the database
     */
    void save(Shop shop);
}
