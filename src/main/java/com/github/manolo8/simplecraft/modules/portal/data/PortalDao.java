package com.github.manolo8.simplecraft.modules.portal.data;

import com.github.manolo8.simplecraft.modules.portal.Portal;
import com.github.manolo8.simplecraft.modules.user.User;

import java.util.List;

public interface PortalDao {

    List<Portal> findAll();

    Portal create(User user, String name);

    void save(Portal portal);
}
