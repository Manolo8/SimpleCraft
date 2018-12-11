package com.github.manolo8.simplecraft.module.mine.block.drops;

import com.github.manolo8.simplecraft.core.data.cache.SaveCache;
import com.github.manolo8.simplecraft.core.data.connection.Database;
import com.github.manolo8.simplecraft.core.data.model.base.BaseDAO;
import com.github.manolo8.simplecraft.core.data.model.base.BaseLoader;
import com.github.manolo8.simplecraft.core.data.model.base.BaseRepository;
import com.github.manolo8.simplecraft.core.data.model.base.DTO;
import com.github.manolo8.simplecraft.core.data.table.OnlyInsert;
import com.github.manolo8.simplecraft.module.mine.block.MineBlock;
import com.github.manolo8.simplecraft.tools.item.ItemRepository;
import org.bukkit.inventory.ItemStack;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class BlockDropRepository extends BaseRepository<BlockDrop,
        BlockDropRepository.BlockDropDTO,
        BlockDropRepository.BlockDropDAO,
        BlockDropRepository.BlockDropCache,
        BlockDropRepository.BlockDropLoader> {

    private final ItemRepository itemRepository;

    //======================================================
    //======================REPOSITORY======================
    //======================================================
    public BlockDropRepository(Database database, ItemRepository itemRepository) {
        super(database);

        this.itemRepository = itemRepository;
    }

    @Override
    protected BlockDropDAO initDao() throws SQLException {
        return new BlockDropDAO(database);
    }

    @Override
    protected BlockDropLoader initLoader() {
        return new BlockDropLoader();
    }

    @Override
    protected BlockDropCache initCache() {
        return new BlockDropCache(this);
    }

    public List<BlockDrop> findByBlockId(int blockId) throws SQLException {
        return findByIdIn(dao.findByBlockId(blockId));
    }

    public BlockDrop create(MineBlock block, ItemStack item, double chance) throws SQLException {
        BlockDropDTO dto = new BlockDropDTO();

        dto.blockId = block.getId();
        dto.chance = chance;
        dto.quantity = item.getAmount();
        dto.itemId = itemRepository.findOrCreateId(item);

        return create(dto);
    }
    //======================================================
    //======================REPOSITORY======================
    //======================================================

    //======================================================
    //==========================DTO=========================
    //======================================================
    class BlockDropDTO extends DTO {

        @OnlyInsert
        private int blockId;
        @OnlyInsert
        private int itemId;
        private double chance;
        private int quantity;
    }
    //======================================================
    //=========================_DTO=========================
    //======================================================


    //======================================================
    //==========================DAO=========================
    //======================================================
    class BlockDropDAO extends BaseDAO<BlockDropDTO> {

        private final String findByBlockIdQuery = "SELECT id FROM MineBlockDrops WHERE blockId=?";

        protected BlockDropDAO(Database database) throws SQLException {
            super(database, "MineBlockDrops", BlockDropDTO.class);
        }

//        @Override
//        protected BlockDropDTO newInstance() {
//            return new BlockDropDTO();
//        }

        public List<Integer> findByBlockId(int blockId) throws SQLException {
            PreparedStatement statement = prepareStatement(findByBlockIdQuery);

            statement.setInt(1, blockId);

            return fromResultListId(statement);
        }
    }
    //======================================================
    //=========================_DAO=========================
    //======================================================


    //======================================================
    //========================CACHE=========================
    //======================================================
    class BlockDropCache extends SaveCache<BlockDrop, BlockDropRepository> {

        public BlockDropCache(BlockDropRepository repository) {
            super(repository);
        }
    }
    //======================================================
    //========================CACHE=========================
    //======================================================


    //======================================================
    //=======================LOADER=========================
    //======================================================
    class BlockDropLoader extends BaseLoader<BlockDrop, BlockDropDTO> {

        @Override
        public BlockDrop newEntity() {
            return new BlockDrop();
        }

        @Override
        public BlockDrop fromDTO(BlockDropDTO dto) throws SQLException {
            BlockDrop drop = super.fromDTO(dto);

            drop.setChance(dto.chance);
            drop.setDrop(itemRepository.findOne(dto.itemId).get().clone());
            drop.getDrop().setAmount(dto.quantity);

            return drop;
        }

        @Override
        public BlockDropDTO toDTO(BlockDrop entity) throws SQLException {
            BlockDropDTO dto = super.toDTO(entity);

            dto.chance = entity.getChance();
            dto.quantity = entity.getDrop().getAmount();

            return dto;
        }
    }
    //======================================================
    //======================_LOADER=========================
    //======================================================
}
