package com.github.manolo8.simplecraft.cache.impl;

import com.github.manolo8.simplecraft.cache.NamedCache;
import com.github.manolo8.simplecraft.cache.SaveCache;
import com.github.manolo8.simplecraft.data.dao.UserDao;
import com.github.manolo8.simplecraft.domain.user.User;

import java.util.UUID;

public class UserCache extends NamedCache<User> implements SaveCache<User> {

    private final UserDao userDao;

    public UserCache(UserDao userDao) {
        super(User.class);
        this.userDao = userDao;
    }

    public User getIfMatch(UUID uuid) {
        for (User t : getCached())
            if (t.getUuid().equals(uuid)) {
                t.setLastCheck(System.currentTimeMillis());
                return t;
            }
        return null;
    }

    @Override
    public void save(User user) {
        userDao.save(user);
    }
}
