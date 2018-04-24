package com.github.manolo8.simplecraft.modules.skill.data;

import com.github.manolo8.simplecraft.modules.skill.Skill;
import com.github.manolo8.simplecraft.modules.user.User;

import java.util.List;

public interface SkillDao {

    List<SkillDTO> findByUser(User user);

    void save(Skill skill);

    void save(List<Skill> skills);

    SkillDTO create(User user, int type);
}
