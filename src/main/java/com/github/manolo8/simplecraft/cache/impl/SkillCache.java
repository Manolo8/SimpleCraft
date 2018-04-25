package com.github.manolo8.simplecraft.cache.impl;

import com.github.manolo8.simplecraft.cache.Cache;
import com.github.manolo8.simplecraft.cache.SaveCache;
import com.github.manolo8.simplecraft.modules.skill.Skill;
import com.github.manolo8.simplecraft.modules.skill.data.SkillDao;

public class SkillCache extends Cache<Skill> implements SaveCache<Skill> {

    private SkillDao skillDao;

    public SkillCache(SkillDao skillDao) {
        super(Skill.class);
        this.skillDao = skillDao;
    }

    @Override
    public void save(Skill skill) {
        skillDao.save(skill);
    }
}
