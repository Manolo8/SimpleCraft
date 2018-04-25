package com.github.manolo8.simplecraft.modules.skill.data;

import com.github.manolo8.simplecraft.cache.Cache;
import com.github.manolo8.simplecraft.modules.skill.Skill;
import com.github.manolo8.simplecraft.modules.skill.types.*;
import com.github.manolo8.simplecraft.modules.user.User;

import java.util.ArrayList;
import java.util.List;

public class SkillRepository {

    public static SkillRepository instance;
    public static List<Skill> types;

    static {
        types = new ArrayList<>();

        types.add(new SkillDodgeDamage());
        types.add(new SkillExtraDamage());
        types.add(new SkillExtraLife());
        types.add(new SkillFallProtection());
        types.add(new SkillMagicAreaDamage());
        types.add(new SkillMagicBigJump());
        types.add(new SkillMagicArrowExplosive());
        types.add(new SkillMagicLifeSteal());
    }

    private SkillDao skillDao;
    private Cache<Skill> skillCache;

    public SkillRepository(Cache<Skill> cache, SkillDao skillDao) {
        this.skillDao = skillDao;
        this.skillCache = cache;
        instance = this;
    }

    public Skill create(User user, int type) {
        Skill skill = fromDTO(skillDao.create(user, type));

        user.getSkills().add(skill);
        skill.addReference();

        return skill;
    }

    public List<Skill> findUserSkills(User user) {
        List<Skill> skills = new ArrayList<>();

        List<SkillDTO> list = skillDao.findByUser(user);

        for (SkillDTO skill : list)
            skills.add(fromDTO(skill));

        return skills;
    }

    public void save(List<Skill> skills) {
        skillDao.save(skills);
    }

    private Skill fromDTO(SkillDTO dto) {
        Skill skill;

        skill = skillCache.getIfMatch(dto.getId());

        if (skill != null) return skill;

        skill = newInstance(dto.getType());

        skill.setLevel(dto.getLevel());
        skill.setId(dto.getId());
        skill.setNeedSave(false);

        skillCache.add(skill);

        return skill;
    }

    public void save(Skill skill) {
        skillDao.save(skill);
    }

    private Skill newInstance(int type) {
        for (Skill skill : types) {
            if (skill.getType() == type) {
                return skill.newInstance();
            }
        }

        return null;
    }
}