package com.github.manolo8.simplecraft.modules.user.data;

import com.github.manolo8.simplecraft.cache.impl.UserCache;
import com.github.manolo8.simplecraft.data.dao.UserDao;
import com.github.manolo8.simplecraft.modules.group.data.GroupRepository;
import com.github.manolo8.simplecraft.modules.plot.data.PlotRepository;
import com.github.manolo8.simplecraft.modules.skill.data.SkillRepository;
import com.github.manolo8.simplecraft.modules.user.User;

import java.util.UUID;

public class UserRepository {

    private final UserCache cache;
    private final UserDao userDao;
    private final GroupRepository groupRepository;
    private final PlotRepository plotRepository;
    private final SkillRepository skillRepository;

    public UserRepository(UserCache cache,
                          UserDao userDao,
                          GroupRepository groupRepository,
                          PlotRepository plotRepository,
                          SkillRepository skillRepository) {
        this.cache = cache;
        this.userDao = userDao;
        this.groupRepository = groupRepository;
        this.plotRepository = plotRepository;
        this.skillRepository = skillRepository;
    }

    public User findOne(int id) {
        User user;
        user = cache.getIfMatch(id);

        if (user != null) return user;

        return fromDTO(userDao.findOne(id));
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
        user.setGroup(groupRepository.findOneOrDefault(userDTO.getGroupId()));
        user.setPlots(plotRepository.findUserPlots(userDTO.getId()));
        user.setSkills(skillRepository.findUserSkills(user));
        user.setExp(userDTO.getExp());
        user.setLevel(userDTO.getLevel());
        user.setNeedSave(false);

        cache.add(user);

        return user;
    }
}
