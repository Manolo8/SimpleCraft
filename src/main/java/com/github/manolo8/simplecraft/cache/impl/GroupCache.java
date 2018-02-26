package com.github.manolo8.simplecraft.cache.impl;

import com.github.manolo8.simplecraft.cache.Cache;
import com.github.manolo8.simplecraft.cache.NamedCache;
import com.github.manolo8.simplecraft.cache.SaveCache;
import com.github.manolo8.simplecraft.data.dao.GroupDao;
import com.github.manolo8.simplecraft.domain.group.Group;

public class GroupCache extends NamedCache<Group> implements SaveCache<Group> {

    private final GroupDao groupDao;

    public GroupCache(GroupDao groupDao) {
        super(Group.class);
        this.groupDao = groupDao;
    }

    @Override
    public void save(Group group) {
        groupDao.save(group);
    }
}
