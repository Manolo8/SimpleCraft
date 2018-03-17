package com.github.manolo8.simplecraft.domain.group;

import com.github.manolo8.simplecraft.domain.group.data.GroupRepository;

public class GroupService {

    private final GroupRepository groupRepository;

    public GroupService(GroupRepository groupRepository) {
        this.groupRepository = groupRepository;
    }

    public Group create(String name) {
        return groupRepository.create(name);
    }

    public Group findOne(String name) {
        return groupRepository.findOne(name);
    }

    public void save(Group group) {
        groupRepository.save(group);
    }

    public void delete(int id) {
        Group group = groupRepository.findOne(id);

        if(group == null) return;

        groupRepository.delete(group);
    }

    public void setDefault(Group aDefault) {

    }
}
