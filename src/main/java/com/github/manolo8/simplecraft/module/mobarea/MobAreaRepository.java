package com.github.manolo8.simplecraft.module.mobarea;

import com.github.manolo8.simplecraft.core.world.WorldInfoRepository;
import com.github.manolo8.simplecraft.core.data.connection.Database;
import com.github.manolo8.simplecraft.core.world.model.container.ContainerDAO;
import com.github.manolo8.simplecraft.core.world.model.container.ContainerDTO;
import com.github.manolo8.simplecraft.core.world.model.container.ContainerLoader;
import com.github.manolo8.simplecraft.core.world.model.container.ContainerRepository;
import com.github.manolo8.simplecraft.core.data.model.named.NamedCache;
import com.github.manolo8.simplecraft.module.mobarea.mobs.MobInfoRepository;
import com.github.manolo8.simplecraft.tools.item.ItemRepository;

import java.sql.SQLException;

public class MobAreaRepository extends ContainerRepository<MobArea,
        MobAreaRepository.MobAreaDTO,
        MobAreaRepository.MobAreaDAO,
        MobAreaRepository.MobAreaCache,
        MobAreaRepository.MobAreaLoader> {

    private final WorldInfoRepository worldInfoRepository;
    private final MobInfoRepository mobInfoRepository;

    //======================================================
    //======================REPOSITORY======================
    //======================================================
    public MobAreaRepository(Database database,
                             ItemRepository itemRepository,
                             WorldInfoRepository worldInfoRepository) throws SQLException {
        super(database);

        this.mobInfoRepository = new MobInfoRepository(database, itemRepository);

        this.worldInfoRepository = worldInfoRepository;
    }

    @Override
    public void init() throws SQLException {
        super.init();

        mobInfoRepository.init();
    }

    @Override
    protected MobAreaDAO initDao() throws SQLException {
        return new MobAreaDAO(database);
    }

    @Override
    protected MobAreaLoader initLoader() {
        return new MobAreaLoader(worldInfoRepository);
    }

    @Override
    protected MobAreaCache initCache() {
        return new MobAreaCache(this);
    }

    public MobInfoRepository getMobInfoRepository() {
        return mobInfoRepository;
    }
    //======================================================
    //=====================_REPOSITORY======================
    //======================================================


    //======================================================
    //==========================DTO=========================
    //======================================================
    class MobAreaDTO extends ContainerDTO {
    }
    //======================================================
    //=========================_DTO=========================
    //======================================================


    //======================================================
    //==========================DAO=========================
    //======================================================
    class MobAreaDAO extends ContainerDAO<MobAreaDTO> {

        MobAreaDAO(Database database) throws SQLException {
            super(database, "MobAreas", MobAreaDTO.class);
        }

    }
    //======================================================
    //=========================_DAO=========================
    //======================================================

    //======================================================
    //==========================CACHE=======================
    //======================================================
    class MobAreaCache extends NamedCache<MobArea, MobAreaRepository> {

        MobAreaCache(MobAreaRepository repository) {
            super(repository);
        }
    }
    //======================================================
    //=========================_CACHE=======================
    //======================================================


    //======================================================
    //=========================LOADER=======================
    //======================================================
    class MobAreaLoader extends ContainerLoader<MobArea, MobAreaDTO> {

        protected MobAreaLoader(WorldInfoRepository worldInfoRepository) {
            super(worldInfoRepository);
        }

        @Override
        public MobArea newEntity() {
            return new MobArea(mobInfoRepository);
        }

        @Override
        public MobArea fromDTO(MobAreaDTO dto) throws SQLException {
            MobArea entity = super.fromDTO(dto);

            entity.setMobInfos(mobInfoRepository.findByMobArea(dto.id));

            return entity;
        }
    }
    //======================================================
    //========================_LOADER=======================
    //======================================================
}
