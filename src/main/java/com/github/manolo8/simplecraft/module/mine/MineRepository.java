package com.github.manolo8.simplecraft.module.mine;

import com.github.manolo8.simplecraft.core.world.WorldInfoRepository;
import com.github.manolo8.simplecraft.core.data.connection.Database;
import com.github.manolo8.simplecraft.core.world.model.container.ContainerDAO;
import com.github.manolo8.simplecraft.core.world.model.container.ContainerDTO;
import com.github.manolo8.simplecraft.core.world.model.container.ContainerLoader;
import com.github.manolo8.simplecraft.core.world.model.container.ContainerRepository;
import com.github.manolo8.simplecraft.core.data.model.named.NamedCache;
import com.github.manolo8.simplecraft.module.mine.block.MineBlockRepository;
import com.github.manolo8.simplecraft.tools.item.ItemRepository;

import java.sql.SQLException;

public class MineRepository extends ContainerRepository<Mine,
        MineRepository.MineDTO,
        MineRepository.MineDAO,
        MineRepository.MineCache,
        MineRepository.MineLoader> {

    private final WorldInfoRepository worldInfoRepository;
    private final MineBlockRepository mineBlockRepository;

    //======================================================
    //======================REPOSITORY======================
    //======================================================
    public MineRepository(Database database,
                          WorldInfoRepository worldInfoRepository,
                          ItemRepository itemRepository) throws SQLException {
        super(database);

        this.worldInfoRepository = worldInfoRepository;
        this.mineBlockRepository = new MineBlockRepository(database, itemRepository);
    }

    @Override
    public void init() throws SQLException {
        super.init();

        mineBlockRepository.init();
    }

    @Override
    protected MineDAO initDao() throws SQLException {
        return new MineDAO(database);
    }

    @Override
    protected MineLoader initLoader() {
        return new MineLoader(worldInfoRepository);
    }

    @Override
    protected MineCache initCache() {
        return new MineCache(this);
    }

    public MineBlockRepository getMineBlockRepository() {
        return mineBlockRepository;
    }
    //======================================================
    //======================REPOSITORY======================
    //======================================================

    //======================================================
    //==========================DTO=========================
    //======================================================
    class MineDTO extends ContainerDTO {

        private int currentOres;

    }
    //======================================================
    //=========================_DTO=========================
    //======================================================


    //======================================================
    //==========================DAO=========================
    //======================================================
    class MineDAO extends ContainerDAO<MineDTO> {

        protected MineDAO(Database database) throws SQLException {
            super(database, "Mines", MineDTO.class);
        }

    }
    //======================================================
    //=========================_DAO=========================
    //======================================================


    //======================================================
    //========================CACHE=========================
    //======================================================
    class MineCache extends NamedCache<Mine, MineRepository> {
        public MineCache(MineRepository repository) {
            super(repository);
        }
    }
    //======================================================
    //========================CACHE=========================
    //======================================================


    //======================================================
    //=======================LOADER=========================
    //======================================================
    class MineLoader extends ContainerLoader<Mine, MineDTO> {

        protected MineLoader(WorldInfoRepository worldInfoRepository) {
            super(worldInfoRepository);
        }

        @Override
        public Mine newEntity() {
            return new Mine(mineBlockRepository);
        }

        @Override
        public Mine fromDTO(MineDTO dto) throws SQLException {
            Mine mine = super.fromDTO(dto);

            mine.setCurrentOres(dto.currentOres);
            mine.setBlocks(mineBlockRepository.findByMineId(dto.id));

            mine.build();

            return mine;
        }

        @Override
        public MineDTO toDTO(Mine entity) throws SQLException {
            MineDTO dto = super.toDTO(entity);

            dto.currentOres = entity.getCurrentOres();

            return dto;
        }
    }
    //======================================================
    //======================_LOADER=========================
    //======================================================
}
