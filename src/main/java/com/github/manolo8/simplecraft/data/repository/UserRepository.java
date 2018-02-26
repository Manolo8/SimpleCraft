package com.github.manolo8.simplecraft.data.repository;

import com.github.manolo8.simplecraft.cache.impl.UserCache;
import com.github.manolo8.simplecraft.data.dao.UserDao;
import com.github.manolo8.simplecraft.data.dto.UserDTO;
import com.github.manolo8.simplecraft.domain.user.User;

import java.util.UUID;

public class UserRepository {

    private final UserCache cache;
    private final UserDao userDao;
    private final GroupRepository groupRepository;

    public UserRepository(UserCache cache,
                          UserDao userDao,
                          GroupRepository groupRepository) {
        this.cache = cache;
        this.userDao = userDao;
        this.groupRepository = groupRepository;
    }

    public User findOne(UUID uuid) {
        User user;
        user = cache.getIfMatch(uuid);

        if (user != null) return user;

        return fromDTO(userDao.findOne(uuid));
    }

    public User findOne(String lastName) {
        User user;
        user = cache.getIfMatch(lastName);

        if (user != null) return user;

        return fromDTO(userDao.findOne(lastName));
    }

    public void save(User user) {
        userDao.save(user);
    }

    /**
     * Carrega um usuário com todas as
     * referencias a outras entidades,
     * salva no sistema de cache em
     * então retorna o usuario
     *
     * @param userDTO dto
     * @return User
     */
    private User fromDTO(UserDTO userDTO) {
        if (userDTO == null) return null;

        User user = new User();

        user.setId(userDTO.getId());
        user.setUuid(userDTO.getUuid());
        user.setName(userDTO.getName());
        user.setMoney(userDTO.getMoney());
        user.setGroup(groupRepository.findOne(userDTO.getGroupId()));

        cache.add(user);

        return user;
    }
}
