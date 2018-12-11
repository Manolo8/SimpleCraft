package com.github.manolo8.simplecraft.module.mine.block;

import com.github.manolo8.simplecraft.core.data.cache.SaveCache;
import com.github.manolo8.simplecraft.core.data.connection.Database;
import com.github.manolo8.simplecraft.core.data.model.base.BaseDAO;
import com.github.manolo8.simplecraft.core.data.model.base.BaseLoader;
import com.github.manolo8.simplecraft.core.data.model.base.BaseRepository;
import com.github.manolo8.simplecraft.core.data.model.base.DTO;
import com.github.manolo8.simplecraft.core.data.table.OnlyInsert;
import com.github.manolo8.simplecraft.module.mine.Mine;
import com.github.manolo8.simplecraft.module.mine.block.drops.BlockDropRepository;
import com.github.manolo8.simplecraft.tools.item.ItemRepository;
import com.github.manolo8.simplecraft.utils.mc.MaterialList;
import org.bukkit.Material;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class MineBlockRepository extends BaseRepository<MineBlock,
        MineBlockRepository.MineBlockDTO,
        MineBlockRepository.MineBlockDAO,
        MineBlockRepository.MineBlockCache,
        MineBlockRepository.MineBlockLoader> {

    private final BlockDropRepository blockDropRepository;

    //======================================================
    //======================REPOSITORY======================
    //======================================================
    public MineBlockRepository(Database database, ItemRepository itemRepository) throws SQLException {
        super(database);

        this.blockDropRepository = new BlockDropRepository(database, itemRepository);
    }

    @Override
    public void init() throws SQLException {
        super.init();

        this.blockDropRepository.init();
    }

    @Override
    protected MineBlockDAO initDao() throws SQLException {
        return new MineBlockDAO(database);
    }

    @Override
    protected MineBlockLoader initLoader() {
        return new MineBlockLoader();
    }

    @Override
    protected MineBlockCache initCache() {
        return new MineBlockCache(this);
    }

    public List<MineBlock> findByMineId(int mineId) throws SQLException {
        return findByIdIn(dao.findByMineId(mineId));
    }

    public MineBlock create(Mine mine, Material material, double percent) throws SQLException {
        MineBlockDTO dto = new MineBlockDTO();

        dto.mineId = mine.getId();
        dto.materialId = MaterialList.toId(material);
        dto.percent = percent;

        return create(dto);
    }

    public BlockDropRepository getBlockDropRepository() {
        return blockDropRepository;
    }
    //======================================================
    //======================REPOSITORY======================
    //======================================================

    //======================================================
    //==========================DTO=========================
    //======================================================
    class MineBlockDTO extends DTO {

        @OnlyInsert
        private int mineId;

        private int materialId;
        private double percent;
    }
    //======================================================
    //=========================_DTO=========================
    //======================================================


    //======================================================
    //==========================DAO=========================
    //======================================================
    class MineBlockDAO extends BaseDAO<MineBlockDTO> {

        private final String findByBlockIdQuery = "SELECT id FROM MineBlocks WHERE mineId=?";

        protected MineBlockDAO(Database database) throws SQLException {
            super(database, "MineBlocks", MineBlockDTO.class);
        }

        public List<Integer> findByMineId(int mineId) throws SQLException {
            PreparedStatement statement = prepareStatement(findByBlockIdQuery);

            statement.setInt(1, mineId);

            return fromResultListId(statement);
        }
    }
    //======================================================
    //=========================_DAO=========================
    //======================================================


    //======================================================
    //========================CACHE=========================
    //======================================================
    class MineBlockCache extends SaveCache<MineBlock, MineBlockRepository> {

        public MineBlockCache(MineBlockRepository repository) {
            super(repository);
        }
    }
    //======================================================
    //========================CACHE=========================
    //======================================================


    //======================================================
    //=======================LOADER=========================
    //======================================================
    class MineBlockLoader extends BaseLoader<MineBlock, MineBlockDTO> {

        @Override
        public MineBlock newEntity() {
            return new MineBlock(blockDropRepository);
        }

        @Override
        public MineBlock fromDTO(MineBlockDTO dto) throws SQLException {
            MineBlock block = super.fromDTO(dto);

            block.setMaterial(MaterialList.fromId(dto.materialId));
            block.setPercent(dto.percent);
            block.setDrops(blockDropRepository.findByBlockId(dto.id));

            return block;
        }

        @Override
        public MineBlockDTO toDTO(MineBlock entity) throws SQLException {
            MineBlockDTO dto = super.toDTO(entity);

            dto.materialId = MaterialList.toId(entity.getMaterial());
            dto.percent = entity.getPercent();

            return dto;
        }
    }
    //======================================================
    //======================_LOADER=========================
    //======================================================
}