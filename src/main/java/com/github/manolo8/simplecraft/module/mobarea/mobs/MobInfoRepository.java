package com.github.manolo8.simplecraft.module.mobarea.mobs;

import com.github.manolo8.simplecraft.core.data.connection.Database;
import com.github.manolo8.simplecraft.core.data.model.named.*;
import com.github.manolo8.simplecraft.core.data.table.OnlyInsert;
import com.github.manolo8.simplecraft.module.mobarea.MobArea;
import com.github.manolo8.simplecraft.module.mobarea.mobs.item.MobDropRepository;
import com.github.manolo8.simplecraft.tools.item.ItemRepository;
import org.bukkit.entity.*;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class MobInfoRepository extends NamedRepository<MobInfo,
        MobInfoRepository.MobInfoDTO,
        MobInfoRepository.MobInfoDAO,
        MobInfoRepository.MobInfoCache,
        MobInfoRepository.MobInfoLoader> {

    private final MobDropRepository mobDropRepository;

    //======================================================
    //======================REPOSITORY======================
    //======================================================
    public MobInfoRepository(Database database, ItemRepository itemRepository) throws SQLException {
        super(database);

        this.mobDropRepository = new MobDropRepository(database, itemRepository);
    }

    @Override
    public void init() throws SQLException {
        super.init();

        mobDropRepository.init();
    }

    @Override
    protected MobInfoDAO initDao() throws SQLException {
        return new MobInfoDAO(database);
    }

    @Override
    protected MobInfoLoader initLoader() {
        return new MobInfoLoader();
    }

    @Override
    protected MobInfoCache initCache() {
        return new MobInfoCache(this);
    }

    public List<MobInfo> findByMobArea(int id) throws SQLException {
        return findByIdIn(dao.findByMobAreaId(id));
    }

    public Mob findMob(String name) {
        for (Mob mob : loader.mobs)
            if (mob.name.equalsIgnoreCase(name))
                return mob;

        return null;
    }

    public Mob[] getMobs() {
        return loader.mobs;
    }

    public MobInfo create(MobArea mobArea, Mob mob, int maxQuantity) throws SQLException {
        MobInfoDTO dto = new MobInfoDTO();

        dto.areaId = mobArea.getId();
        dto.name = mob.name;
        dto.fastName = mob.name.toLowerCase();
        dto.displayName = mob.name;
        dto.mobId = mob.id;
        dto.maxQuantity = maxQuantity;

        return create(dto);
    }

    public MobDropRepository getMobDropRepository() {
        return mobDropRepository;
    }
    //======================================================
    //=====================_REPOSITORY======================
    //======================================================


    //======================================================
    //==========================DTO=========================
    //======================================================
    class MobInfoDTO extends NamedDTO {

        @OnlyInsert
        private int areaId;

        private String displayName;
        private int mobId;
        private int maxQuantity;
        private int life;
        private int lrange;
        private int damage;
        private int exp;
        private double speed;

    }
    //======================================================
    //=========================_DTO=========================
    //======================================================


    //======================================================
    //==========================DAO=========================
    //======================================================
    class MobInfoDAO extends NamedDAO<MobInfoDTO> {

        private final String findByMobAreaId = "SELECT id FROM MobAreaInfos WHERE areaId=?";

        MobInfoDAO(Database database) throws SQLException {
            super(database, "MobAreaInfos", MobInfoDTO.class);
        }

        public List<Integer> findByMobAreaId(int id) throws SQLException {
            PreparedStatement statement = prepareStatement(findByMobAreaId);

            statement.setInt(1, id);

            return fromResultListId(statement);
        }
    }
    //======================================================
    //=========================_DAO=========================
    //======================================================

    //======================================================
    //==========================CACHE=======================
    //======================================================
    class MobInfoCache extends NamedCache<MobInfo, MobInfoRepository> {

        MobInfoCache(MobInfoRepository repository) {
            super(repository);
        }
    }
    //======================================================
    //=========================_CACHE=======================
    //======================================================


    //======================================================
    //=========================LOADER=======================
    //======================================================
    class MobInfoLoader extends NamedLoader<MobInfo, MobInfoDTO> {

        private Mob[] mobs;

        public MobInfoLoader() {
            mobs = new Mob[14];

            mobs[0] = new Mob(0, "zombie", Zombie.class);
            mobs[1] = new Mob(1, "creeper", Creeper.class);
            mobs[2] = new Mob(2, "pigzombie", PigZombie.class);
            mobs[3] = new Mob(3, "vindicator", Vindicator.class);
            mobs[4] = new Mob(4, "witherskeleton", WitherSkeleton.class);
            mobs[5] = new Mob(5, "enderman", Enderman.class);
            mobs[6] = new Mob(6, "witch", Witch.class);
            mobs[7] = new Mob(7, "spider", Spider.class);
            mobs[8] = new Mob(8, "skeleton", Skeleton.class);
            mobs[9] = new Mob(9, "blaze", Blaze.class);
            mobs[10] = new Mob(10, "cow", Cow.class);
            mobs[11] = new Mob(11, "sheep", Sheep.class);
            mobs[12] = new Mob(12, "chicken", Chicken.class);
            mobs[13] = new Mob(13, "magmacube", MagmaCube.class);

        }

        @Override
        public MobInfo newEntity() {
            return new MobInfo(mobDropRepository);
        }

        @Override
        public MobInfo fromDTO(MobInfoDTO dto) throws SQLException {
            MobInfo entity = super.fromDTO(dto);

            entity.setMob(mobs[dto.mobId]);

            entity.setDisplayName(dto.displayName);
            entity.setMaxQuantity(dto.maxQuantity);
            entity.setDamage(dto.damage);
            entity.setSpeed(dto.speed);
            entity.setRange(dto.lrange);
            entity.setExp(dto.exp);
            entity.setLife(dto.life);
            entity.setDrops(mobDropRepository.findByMobId(dto.id));

            return entity;
        }

        @Override
        public MobInfoDTO toDTO(MobInfo entity) throws SQLException {
            MobInfoDTO dto = super.toDTO(entity);

            dto.mobId = entity.getMob().id;

            dto.displayName = entity.getDisplayName();
            dto.maxQuantity = entity.getMaxQuantity();
            dto.damage = entity.getDamage();
            dto.speed = entity.getSpeed();
            dto.lrange = entity.getRange();
            dto.exp = entity.getExp();
            dto.life = entity.getLife();

            return dto;
        }
    }
    //======================================================
    //========================_LOADER=======================
    //======================================================
}
