package com.github.manolo8.simplecraft.data.dao;

import com.github.manolo8.simplecraft.data.dto.UserDTO;
import com.github.manolo8.simplecraft.domain.user.User;

import java.util.UUID;

public interface UserDao {

    /**
     * @param uuid identification
     * @return the UserDTO
     * if does not exists, create
     * a new one
     */
    UserDTO findOne(UUID uuid);

    /**
     * @param id identification
     * @return the UserDTO
     * if does not exists, return
     * null
     */
    UserDTO findOne(Integer id);

    /**
     * @param lastName ultimo nome
     * @return an user to this name
     * if does not exits, return null
     */
    UserDTO findOne(String lastName);

    /**
     * Save an user in the database
     * the user with all UUID references
     *
     * @param user the user
     */
    void save(User user);
}
