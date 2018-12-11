package com.github.manolo8.simplecraft.module.skill;

import com.github.manolo8.simplecraft.core.data.cache.SaveCache;
import com.github.manolo8.simplecraft.core.data.connection.Database;
import com.github.manolo8.simplecraft.core.data.model.base.BaseDAO;
import com.github.manolo8.simplecraft.core.data.model.base.BaseRepository;
import com.github.manolo8.simplecraft.core.data.model.base.DTO;
import com.github.manolo8.simplecraft.core.data.model.base.Loader;
import com.github.manolo8.simplecraft.module.skill.types.*;
import com.github.manolo8.simplecraft.module.skill.user.SkillUserRepository;
import com.github.manolo8.simplecraft.module.user.identity.Identity;
import com.github.manolo8.simplecraft.module.user.identity.IdentityRepository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class SkillRepository extends BaseRepository<Skill,
        SkillRepository.SkillDTO,
        SkillRepository.SkillDAO,
        SkillRepository.SkillCache,
        SkillRepository.SkillLoader> {

    private final IdentityRepository identityRepository;
    private final SkillUserRepository skillUserRepository;

    //======================================================
    //======================REPOSITORY======================
    //======================================================
    public SkillRepository(Database database, IdentityRepository identityRepository) throws SQLException {
        super(database);

        this.identityRepository = identityRepository;
        this.skillUserRepository = new SkillUserRepository(database, this, identityRepository);
    }

    @Override
    public void init() throws SQLException {
        super.init();

        this.skillUserRepository.init();
    }

    public Skill[] getTypes() {
        return loader.skills;
    }

    @Override
    protected SkillDAO initDao() throws SQLException {
        return new SkillDAO(database);
    }

    @Override
    protected SkillLoader initLoader() {
        return new SkillLoader();
    }

    @Override
    protected SkillCache initCache() {
        return new SkillCache(this);
    }

    public Skill create(Identity owner, int type) throws SQLException {
        SkillDTO dto = new SkillDTO();

        dto.ownerId = owner.getId();
        dto.type = type;
        dto.active = false;
        dto.level = 1;

        return create(dto);
    }

    public List<Skill> findByOwner(Identity owner) throws SQLException {
        return findByIdIn(dao.findByOwnerId(owner.getId()));
    }

    public SkillUserRepository getSkillUserRepository() {
        return skillUserRepository;
    }

    //======================================================
    //=====================_REPOSITORY======================
    //======================================================


    //======================================================
    //==========================DTO=========================
    //======================================================
    class SkillDTO extends DTO {

        private int ownerId;
        private int type;
        private int level;
        private boolean active;

    }
    //======================================================
    //=========================_DTO=========================
    //======================================================


    //======================================================
    //==========================DAO=========================
    //======================================================
    class SkillDAO extends BaseDAO<SkillDTO> {

        private final String findByOwnerIdQuery = "SELECT id FROM Skills WHERE ownerId=?";

        SkillDAO(Database database) throws SQLException {
            super(database, "Skills", SkillDTO.class);
        }

        public List<Integer> findByOwnerId(int ownerId) throws SQLException {
            PreparedStatement statement = prepareStatement(findByOwnerIdQuery);

            statement.setInt(1, ownerId);

            return fromResultListId(statement);
        }
    }
    //======================================================
    //=========================_DAO=========================
    //======================================================

    //======================================================
    //==========================CACHE=======================
    //======================================================
    class SkillCache extends SaveCache<Skill, SkillRepository> {

        SkillCache(SkillRepository repository) {
            super(repository);
        }

    }
    //======================================================
    //=========================_CACHE=======================
    //======================================================


    //======================================================
    //=========================LOADER=======================
    //======================================================
    class SkillLoader implements Loader<Skill, SkillDTO> {

        private Skill[] skills;

        public SkillLoader() {
            skills = new Skill[9];

            skills[0] = new SkillExtraDamage();
            skills[1] = new SkillArmorDamage();
            skills[2] = new SkillArmorDurability();
            skills[3] = new SkillResistance();
            skills[4] = new SkillAbsorption();
            skills[5] = new SkillFallProtection();
            skills[6] = new SkillMagicBigJump();
            skills[7] = new SkillMagicArrowExplosive();
            skills[8] = new SkillAutoPlant();
        }

        @Override
        public Skill fromDTO(SkillDTO dto) throws SQLException {
            Skill skill = skills[dto.type].newInstance();

            skill.setId(dto.id);
            skill.setOwner(identityRepository.findOne(dto.ownerId));
            skill.setActive(dto.active);
            skill.setLevel(dto.level);

            return skill;
        }

        @Override
        public SkillDTO toDTO(Skill entity) throws SQLException {
            SkillDTO dto = new SkillDTO();

            dto.id = entity.getId();
            dto.ownerId = entity.getOwner().getId();
            dto.active = entity.isActive();
            dto.level = entity.getLevel();
            dto.type = entity.getType();

            return dto;
        }
    }
    //======================================================
    //========================_LOADER=======================
    //======================================================
}
