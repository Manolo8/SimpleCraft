package com.github.manolo8.simplecraft.domain.group.data;

import com.github.manolo8.simplecraft.cache.impl.GroupCache;
import com.github.manolo8.simplecraft.data.dao.GroupDao;
import com.github.manolo8.simplecraft.domain.group.Group;

import java.util.ArrayList;

public class GroupRepository {

    private final GroupCache groupCache;
    private final GroupDao groupDao;

    public GroupRepository(GroupCache groupCache,
                           GroupDao groupDao) {
        this.groupCache = groupCache;
        this.groupDao = groupDao;
    }

    public Group findOneOrDefault(Integer id) {
        Group group = findOne(id);

        return group == null ? findDefaultGroup() : group;
    }

    public Group findOne(Integer id) {
        if (id == null) return null;

        Group group;
        group = groupCache.getIfMatch(id);

        if (group != null) return group;

        group = fromDTO(groupDao.findOne(id));

        return group;
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

    private Group findDefaultGroup() {
        //Procura por um grupo default no cache
        for (Group group : groupCache.getCached()) if (group.isDefault()) return group;

        //Se não encontra, procura no banco
        GroupDTO def = groupDao.findDefaultGroup();

        if (def != null) return fromDTO(def);

        //Se não encontrar, cria um novo
        def = groupDao.create("DEFAULT");

        def.setDefault(true);
        def.setPermissions(new ArrayList<>());
        def.setName("DEFAULT");
        def.setTag("DEFAULT");

        Group group =  fromDTO(def);
        save(group);
        return group;
    }

    private Group fromDTO(GroupDTO groupDTO) {
        if (groupDTO == null) return null;

        Group group = new Group();
        group.setId(groupDTO.getId());
        group.setName(groupDTO.getName());
        group.setTag(groupDTO.getTag());
        group.setDefault(groupDTO.isDefault());
        group.setParent(findOne(groupDTO.getParentId()));
        group.setPermissions(groupDTO.getPermissions());
        group.setNeedSave(false);

        groupCache.add(group);

        return group;
    }
}
