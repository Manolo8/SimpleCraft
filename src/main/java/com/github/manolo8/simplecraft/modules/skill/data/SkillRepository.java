package com.github.manolo8.simplecraft.modules.skill.data;

import com.github.manolo8.simplecraft.modules.skill.Skill;
import com.github.manolo8.simplecraft.modules.skill.types.SkillDodgeDamage;
import com.github.manolo8.simplecraft.modules.skill.types.SkillExtraDamage;
import com.github.manolo8.simplecraft.modules.skill.types.SkillExtraLife;
import com.github.manolo8.simplecraft.modules.user.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SkillRepository {

    private SkillDao skillDao;
    private Random random;

    public SkillRepository(SkillDao skillDao, Random random) {
        this.skillDao = skillDao;
        this.random = random;
    }

    public void load(User user) {
        List<Skill> skills = new ArrayList<>();

        List<SkillDTO> list = skillDao.findByUser(user);

        for (SkillDTO skill : list)
            skills.add(fromDTO(skill));

        user.setSkills(skills);
        user.cleanupSkills();
    }

    public void save(User user) {
        skillDao.save(user.getSkills());
    }

    private Skill fromDTO(SkillDTO dto) {
        Skill skill = newInstance(dto.getType());

        skill.setLevel(dto.getLevel());
        skill.setId(dto.getId());

        return skill;
    }

    private Skill newInstance(int type) {
        switch (type) {
            case 0:
                return new SkillDodgeDamage(random);
            case 1:
                return new SkillExtraDamage(random);
            case 2:
                return new SkillExtraLife(random);
            default:
                throw new RuntimeException();
        }
    }
}