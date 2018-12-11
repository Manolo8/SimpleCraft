package com.github.manolo8.simplecraft.module.warp;

import com.github.manolo8.simplecraft.core.world.WorldInfoRepository;
import com.github.manolo8.simplecraft.core.data.connection.Database;
import com.github.manolo8.simplecraft.core.data.model.named.*;
import com.github.manolo8.simplecraft.utils.location.SimpleLocation;
import com.github.manolo8.simplecraft.utils.mc.MaterialList;

import java.sql.SQLException;

public class WarpRepository extends NamedRepository<Warp,
        WarpRepository.WarpDTO,
        WarpRepository.WarpDAO,
        WarpRepository.WarpCache,
        WarpRepository.WarpLoader> {

    private final WorldInfoRepository worldInfoRepository;

    //======================================================
    //======================REPOSITORY======================
    //======================================================
    public WarpRepository(Database database, WorldInfoRepository worldInfoRepository) {
        super(database);

        this.worldInfoRepository = worldInfoRepository;
    }

    @Override
    protected WarpDAO initDao() throws SQLException {
        return new WarpDAO(database);
    }

    @Override
    protected WarpLoader initLoader() {
        return new WarpLoader(worldInfoRepository);
    }

    @Override
    protected WarpCache initCache() {
        return new WarpCache(this);
    }
    //======================================================
    //=====================_REPOSITORY======================
    //======================================================


    //======================================================
    //==========================DTO=========================
    //======================================================
    class WarpDTO extends NamedDTO {

        private int worldId;
        private int x;
        private int y;
        private int z;
        private int yaw;
        private int pitch;

        private int minRank;
        private int icon;
        private int slot;
    }
    //======================================================
    //=========================_DTO=========================
    //======================================================


    //======================================================
    //==========================DAO=========================
    //======================================================
    class WarpDAO extends NamedDAO<WarpDTO> {

        WarpDAO(Database database) throws SQLException {
            super(database, "Warps", WarpDTO.class);
        }

    }
    //======================================================
    //=========================_DAO=========================
    //======================================================

    //======================================================
    //==========================CACHE=======================
    //======================================================
    class WarpCache extends NamedCache<Warp, WarpRepository> {

        WarpCache(WarpRepository repository) {
            super(repository);
        }
    }
    //======================================================
    //=========================_CACHE=======================
    //======================================================


    //======================================================
    //=========================LOADER=======================
    //======================================================
    class WarpLoader extends NamedLoader<Warp, WarpDTO> {

        private final WorldInfoRepository worldInfoRepository;

        WarpLoader(WorldInfoRepository worldInfoRepository) {
            this.worldInfoRepository = worldInfoRepository;
        }

        @Override
        public Warp newEntity() {
            return new Warp();
        }

        @Override
        public Warp fromDTO(WarpDTO dto) throws SQLException {
            Warp entity = super.fromDTO(dto);

            entity.setWorldInfo(worldInfoRepository.findOne(dto.worldId));

            entity.setLocation(new SimpleLocation(dto.x, dto.y, dto.z, dto.yaw, dto.pitch));

            entity.setIcon(MaterialList.fromId(dto.icon));
            entity.setSlot(dto.slot);
            entity.setMinRank(dto.minRank);

            return entity;
        }

        @Override
        public WarpDTO toDTO(Warp entity) throws SQLException {
            WarpDTO dto = super.toDTO(entity);

            dto.worldId = entity.getWorldInfo().getId();

            SimpleLocation loc = entity.getLocation();

            dto.x = loc.getX();
            dto.y = loc.getY();
            dto.z = loc.getZ();
            dto.yaw = (int) loc.getYaw();
            dto.pitch = (int) loc.getPitch();

            dto.icon = MaterialList.toId(entity.getIcon());
            dto.slot = entity.getSlot();
            dto.minRank = entity.getMinRank();

            return dto;
        }
    }
    //======================================================
    //========================_LOADER=======================
    //======================================================
}
