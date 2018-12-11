package com.github.manolo8.simplecraft.module.skill.user;

import com.github.manolo8.simplecraft.core.data.connection.Database;
import com.github.manolo8.simplecraft.module.user.model.identity.*;
import com.github.manolo8.simplecraft.module.skill.SkillRepository;
import com.github.manolo8.simplecraft.module.user.identity.IdentityRepository;

import java.sql.SQLException;

public class SkillUserRepository extends BaseIdentityRepository<SkillUser,
        SkillUserRepository.SkillUserDTO,
        SkillUserRepository.SkillUserDAO,
        SkillUserRepository.SkillUserCache,
        SkillUserRepository.SkillUserLoader> {

    private final SkillRepository skillRepository;

    //======================================================
    //======================REPOSITORY======================
    //======================================================
    public SkillUserRepository(Database database,
                               SkillRepository skillRepository,
                               IdentityRepository identityRepository) {
        super(database, identityRepository);

        this.skillRepository = skillRepository;
    }

    @Override
    protected SkillUserDAO initDao() throws SQLException {
        return new SkillUserDAO(database);
    }

    @Override
    protected SkillUserLoader initLoader() {
        return new SkillUserLoader(identityRepository);
    }

    @Override
    protected SkillUserCache initCache() {
        return new SkillUserCache(this);
    }
    //======================================================
    //=====================_REPOSITORY======================
    //======================================================


    //======================================================
    //==========================DTO=========================
    //======================================================
    class SkillUserDTO extends BaseIdentityDTO {

        private long exp;
        private int level;
        private double mana;

    }
    //======================================================
    //=========================_DTO=========================
    //======================================================


    //======================================================
    //==========================DAO=========================
    //======================================================
    class SkillUserDAO extends BaseIdentityDAO<SkillUserDTO> {

        SkillUserDAO(Database database) throws SQLException {
            super(database, "SkillUsers", SkillUserDTO.class);
        }

    }
    //======================================================
    //=========================_DAO=========================
    //======================================================

    //======================================================
    //==========================CACHE=======================
    //======================================================
    class SkillUserCache extends BaseIdentityCache<SkillUser, SkillUserRepository> {

        SkillUserCache(SkillUserRepository repository) {
            super(repository);
        }
    }
    //======================================================
    //=========================_CACHE=======================
    //======================================================


    //======================================================
    //=========================LOADER=======================
    //======================================================
    class SkillUserLoader extends BaseIdentityLoader<SkillUser, SkillUserDTO> {

        public SkillUserLoader(IdentityRepository identityRepository) {
            super(identityRepository);
        }

        @Override
        public SkillUser newEntity() {
            return new SkillUser();
        }

        @Override
        public SkillUser fromDTO(SkillUserDTO dto) throws SQLException {
            SkillUser entity = super.fromDTO(dto);

            entity.setExp(dto.exp);
            entity.setLevel(dto.level);
            entity.setMana(dto.mana);

            entity.setSkills(skillRepository.findByOwner(entity.getIdentity()));

            entity.cleanupSkills();

            return entity;
        }

        @Override
        public SkillUserDTO toDTO(SkillUser entity) throws SQLException {
            SkillUserDTO dto = super.toDTO(entity);

            dto.exp = entity.getExp();
            dto.level = entity.getLevel();
            dto.mana = entity.getMana();

            return dto;
        }
    }
    //======================================================
    //========================_LOADER=======================
    //======================================================
}
