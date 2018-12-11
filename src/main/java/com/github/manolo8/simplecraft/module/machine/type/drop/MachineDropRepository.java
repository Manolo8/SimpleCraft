package com.github.manolo8.simplecraft.module.machine.type.drop;

import com.github.manolo8.simplecraft.core.data.model.base.BaseDAO;
import com.github.manolo8.simplecraft.core.data.model.base.BaseLoader;
import com.github.manolo8.simplecraft.core.data.model.base.BaseRepository;
import com.github.manolo8.simplecraft.core.data.model.base.DTO;
import com.github.manolo8.simplecraft.core.data.cache.SaveCache;
import com.github.manolo8.simplecraft.core.data.connection.Database;
import com.github.manolo8.simplecraft.core.data.table.OnlyInsert;
import com.github.manolo8.simplecraft.tools.item.ItemRepository;

import java.sql.SQLException;

public class MachineDropRepository extends BaseRepository<MachineDrop,
        MachineDropRepository.MachineDropDTO,
        MachineDropRepository.MachineDropDAO,
        MachineDropRepository.MachineDropCache,
        MachineDropRepository.MachineDropLoader> {

    private final ItemRepository itemRepository;

    //======================================================
    //======================REPOSITORY======================
    //======================================================
    public MachineDropRepository(Database database, ItemRepository itemRepository) {
        super(database);

        this.itemRepository = itemRepository;
    }

    @Override
    protected MachineDropDAO initDao() throws SQLException {
        return new MachineDropDAO(database);
    }

    @Override
    protected MachineDropLoader initLoader() {
        return new MachineDropLoader();
    }

    @Override
    protected MachineDropCache initCache() {
        return new MachineDropCache(this);
    }
    //======================================================
    //=====================_REPOSITORY======================
    //======================================================


    //======================================================
    //==========================DTO=========================
    //======================================================
    class MachineDropDTO extends DTO {

        @OnlyInsert
        private int typeId;
        private int rarity;
        private int itemId;
    }
    //======================================================
    //=========================_DTO=========================
    //======================================================


    //======================================================
    //==========================DAO=========================
    //======================================================
    class MachineDropDAO extends BaseDAO<MachineDropDTO> {

        private final String findByTypeIdQuery = "SELECT id FROM MachineDrops WHERE typeId=?";

        MachineDropDAO(Database database) throws SQLException {
            super(database, "MachineDrops", MachineDropDTO.class);
        }

        

    }
    //======================================================
    //=========================_DAO=========================
    //======================================================

    //======================================================
    //==========================CACHE=======================
    //======================================================
    class MachineDropCache extends SaveCache<MachineDrop, MachineDropRepository> {

        MachineDropCache(MachineDropRepository repository) {
            super(repository);
        }
    }
    //======================================================
    //=========================_CACHE=======================
    //======================================================


    //======================================================
    //=========================LOADER=======================
    //======================================================
    class MachineDropLoader extends BaseLoader<MachineDrop, MachineDropDTO> {

        @Override
        public MachineDrop newEntity() {
            return new MachineDrop();
        }

        @Override
        public MachineDrop fromDTO(MachineDropDTO dto) throws SQLException {
            MachineDrop entity = super.fromDTO(dto);

            entity.setItem(itemRepository.findOne(dto.itemId).copy());
            entity.setRarity(dto.rarity);
            entity.setTypeId(dto.typeId);

            return entity;
        }

        @Override
        public MachineDropDTO toDTO(MachineDrop entity) throws SQLException {
            MachineDropDTO dto = super.toDTO(entity);

            dto.itemId = itemRepository.findOrCreateId(entity.getItem());
            dto.rarity = entity.getRarity();

            return dto;
        }
    }
    //======================================================
    //========================_LOADER=======================
    //======================================================
}
