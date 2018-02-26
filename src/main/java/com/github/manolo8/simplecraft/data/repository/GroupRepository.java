package com.github.manolo8.simplecraft.data.repository;

import com.github.manolo8.simplecraft.cache.impl.GroupCache;
import com.github.manolo8.simplecraft.data.dao.GroupDao;
import com.github.manolo8.simplecraft.data.dto.GroupDTO;
import com.github.manolo8.simplecraft.domain.group.Group;

import java.util.UUID;

public class GroupRepository {

    private final GroupCache groupCache;
    private final GroupDao groupDao;

    public GroupRepository(GroupCache groupCache,
                           GroupDao groupDao) {
        this.groupCache = groupCache;
        this.groupDao = groupDao;
    }

    public Group findOne(Integer id) {
        if (id == null) return null;

        Group group;
        group = groupCache.getIfMatch(id);

        if (group != null) return group;

        return fromDTO(groupDao.findOne(id));
    }


    public Group findOne(String name) {
        if (name == null) return null;

        Group group;
        group = groupCache.getIfMatch(name);

        if (group != null) return group;

        return fromDTO(groupDao.findOne(name));
    }

    public void save(Group group) {
        groupDao.save(group);
    }

    public Group create(String name) {
        Group group = findOne(name);
        if (group != null) return group;
        return fromDTO(groupDao.create(name));
    }

    public void delete(Group group) {
        group.removeReference();
        groupDao.delete(group.getId());
    }

    public Group fromDTO(GroupDTO groupDTO) {
        if (groupDTO == null) return null;

        Group group = new Group();
        group.setId(groupDTO.getId());
        group.setName(groupDTO.getName());
        group.setTag(groupDTO.getTag());
        group.setDefault(groupDTO.isDefault());
        group.setParent(findOne(groupDTO.getParentId()));
        group.setPermissions(groupDTO.getPermissions());

        groupCache.add(group);

        return group;
    }
}
