package com.github.manolo8.simplecraft.module.portal;

import com.github.manolo8.simplecraft.core.world.WorldInfoRepository;
import com.github.manolo8.simplecraft.core.data.connection.Database;
import com.github.manolo8.simplecraft.core.world.model.container.ContainerDAO;
import com.github.manolo8.simplecraft.core.world.model.container.ContainerDTO;
import com.github.manolo8.simplecraft.core.world.model.container.ContainerLoader;
import com.github.manolo8.simplecraft.core.world.model.container.ContainerRepository;
import com.github.manolo8.simplecraft.core.data.model.named.NamedCache;
import com.github.manolo8.simplecraft.utils.location.SimpleLocation;

import java.sql.SQLException;

public class PortalRepository extends ContainerRepository<Portal,
        PortalRepository.PortalDTO,
        PortalRepository.PortalDAO,
        PortalRepository.PortalCache,
        PortalRepository.PortalLoader> {

    private final WorldInfoRepository worldInfoRepository;

    //======================================================
    //=======================REPOSITORY=====================
    //======================================================
    public PortalRepository(Database database, WorldInfoRepository worldInfoRepository) {
        super(database);

        this.worldInfoRepository = worldInfoRepository;
    }

    @Override
    protected PortalDAO initDao() throws SQLException {
        return new PortalDAO(database);
    }

    @Override
    protected PortalLoader initLoader() {
        return new PortalLoader(worldInfoRepository);
    }

    @Override
    protected PortalCache initCache() {
        return new PortalCache(this);
    }

    //======================================================
    //=======================REPOSITORY=====================
    //======================================================

    //======================================================
    //==========================DTO=========================
    //======================================================
    class PortalDTO extends ContainerDTO {

        private int x;
        private int y;
        private int z;
        //Salva em INT mesmo '-'
        private int yaw;
        private int pitch;
        private String message;
    }
    //======================================================
    //=========================_DTO=========================
    //======================================================

    //======================================================
    //=========================DAO==========================
    //======================================================
    class PortalDAO extends ContainerDAO<PortalDTO> {

        protected PortalDAO(Database database) throws SQLException {
            super(database, "Portals", PortalDTO.class);
        }

    }
    //======================================================
    //=========================_DAO=========================
    //======================================================

    //======================================================
    //========================CACHE=========================
    //======================================================
    class PortalCache extends NamedCache<Portal, PortalRepository> {

        public PortalCache(PortalRepository repository) {
            super(repository);
        }
    }
    //======================================================
    //=======================_CACHE=========================
    //======================================================

    //======================================================
    //=======================LOADER=========================
    //======================================================
    class PortalLoader extends ContainerLoader<Portal, PortalDTO> {

        protected PortalLoader(WorldInfoRepository worldInfoRepository) {
            super(worldInfoRepository);
        }

        @Override
        public Portal newEntity() {
            return new Portal();
        }

        @Override
        public Portal fromDTO(PortalDTO dto) throws SQLException {
            Portal portal = super.fromDTO(dto);

            portal.setLocation(new SimpleLocation(dto.x, dto.y, dto.z, dto.yaw, dto.pitch));
            portal.setMessage(dto.message);

            return portal;
        }

        @Override
        public PortalDTO toDTO(Portal entity) throws SQLException {
            PortalDTO dto = super.toDTO(entity);

            dto.x = entity.getLocation().getX();
            dto.y = entity.getLocation().getY();
            dto.z = entity.getLocation().getZ();
            dto.yaw = (int) entity.getLocation().getYaw();
            dto.pitch = (int) entity.getLocation().getPitch();
            dto.message = entity.getMessage();

            return dto;
        }
    }
    //======================================================
    //======================_LOADER=========================
    //======================================================

}
