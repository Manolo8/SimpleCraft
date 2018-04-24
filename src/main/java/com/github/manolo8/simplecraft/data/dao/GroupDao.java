package com.github.manolo8.simplecraft.data.dao;

import com.github.manolo8.simplecraft.modules.group.Group;
import com.github.manolo8.simplecraft.modules.group.data.GroupDTO;

public interface GroupDao {

    /**
     * @param id identification
     * @return the Group
     * if does not exists, return null
     */
    GroupDTO findOne(Integer id);

    /**
     * @param name name
     * @return an user to this name
     * if does not exits, return null
     */
    GroupDTO findOne(String name);

    /**
     * @return the default group, or null
     * if does not exists
     */
    GroupDTO findDefaultGroup();

    /**
     * @param name of the group
     * @return a new group with the
     * name
     */
    GroupDTO create(String name);

    /**
     * Remove an group from the database
     * including all permissions relationed
     * to him
     *
     * @param id do grupo
     */
    void delete(Integer id);

    /**
     * Save an user in the database
     * the user with all UUID references
     *
     * @param group the group
     */
    void save(Group group);
}
