package com.github.manolo8.simplecraft.core.world;

import com.github.manolo8.simplecraft.core.data.connection.Database;
import com.github.manolo8.simplecraft.core.data.model.named.*;
import com.github.manolo8.simplecraft.core.data.table.OnlyInsert;
import com.github.manolo8.simplecraft.core.data.table.Size;
import com.github.manolo8.simplecraft.module.plot.generator.PlotGenerator;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;

import java.sql.SQLException;

public class WorldInfoRepository extends NamedRepository<WorldInfo,
        WorldInfoRepository.WorldInfoDTO,
        WorldInfoRepository.WorldInfoDAO,
        WorldInfoRepository.WorldInfoCache,
        WorldInfoRepository.WorldInfoLoader> {

    //======================================================
    //======================REPOSITORY======================
    //======================================================

    public final WorldBuilder[] builders;

    public WorldInfoRepository(Database database) {
        super(database);

        builders = new WorldBuilder[5];

        builders[0] = new WorldBuilder(0, "overworld", Material.WATER, 0, 2, 3, 4, 5) {
            @Override
            WorldCreator create(String name) {
                return new WorldCreator(name);
            }
        };
        builders[1] = new WorldBuilder(1, "nether", Material.NETHERRACK, 0) {
            @Override
            WorldCreator create(String name) {
                WorldCreator creator = new WorldCreator(name);

                creator.environment(World.Environment.NETHER);

                return creator;
            }
        };
        builders[2] = new WorldBuilder(2, "the_end", Material.END_STONE, 0) {
            @Override
            WorldCreator create(String name) {
                WorldCreator creator = new WorldCreator(name);

                creator.environment(World.Environment.THE_END);

                return creator;
            }
        };
        builders[3] = new WorldBuilder(3, "flat", Material.GRASS_BLOCK, 0) {
            @Override
            WorldCreator create(String name) {
                WorldCreator creator = new WorldCreator(name);

                creator.type(WorldType.FLAT);

                return creator;
            }
        };
        builders[4] = new WorldBuilder(4, "plot", Material.GRASS_BLOCK, 1) {
            @Override
            WorldCreator create(String name) {
                return new PlotGenerator(name);
            }
        };
    }

    @Override
    protected WorldInfoDAO initDao() throws SQLException {
        return new WorldInfoDAO(database);
    }

    @Override
    protected WorldInfoLoader initLoader() {
        return new WorldInfoLoader();
    }

    @Override
    protected WorldInfoCache initCache() {
        return new WorldInfoCache(this);
    }

    public WorldInfo findOrCreate(String name) throws SQLException {

        WorldInfo info = findByName(name);

        return info == null ? create(name) : info;
    }

    public WorldInfo create(String name, WorldBuilder builder) throws SQLException {

        WorldInfoDTO dto = new WorldInfoDTO();

        dto.name = name;
        dto.fastName = name.toLowerCase();
        dto.creatorId = builder.getId();
        dto.flag = builder.flags();

        return create(dto);
    }
    //======================================================
    //=====================_REPOSITORY======================
    //======================================================


    //======================================================
    //==========================DTO=========================
    //======================================================
    class WorldInfoDTO extends NamedDTO {

        @Size(2)
        private byte[] flag;
        @OnlyInsert
        private int creatorId;
        private boolean disabled;
    }
    //======================================================
    //=========================_DTO=========================
    //======================================================


    //======================================================
    //==========================DAO=========================
    //======================================================
    class WorldInfoDAO extends NamedDAO<WorldInfoDTO> {

        protected WorldInfoDAO(Database database) throws SQLException {
            super(database, "WorldInfos", WorldInfoDTO.class);
        }

    }
    //======================================================
    //=========================_DAO=========================
    //======================================================


    //======================================================
    //==========================CACHE=======================
    //======================================================
    class WorldInfoCache extends NamedCache<WorldInfo, WorldInfoRepository> {

        public WorldInfoCache(WorldInfoRepository repository) {
            super(repository);
        }
    }
    //======================================================
    //=========================_CACHE=======================
    //======================================================


    //======================================================
    //=========================LOADER=======================
    //======================================================
    class WorldInfoLoader extends NamedLoader<WorldInfo, WorldInfoDTO> {

        @Override
        public WorldInfo newEntity() {
            return new WorldInfo();
        }

        @Override
        public WorldInfo fromDTO(WorldInfoDTO dto) throws SQLException {
            WorldInfo info = super.fromDTO(dto);

            info.setFlag(new WorldFlag(dto.flag));
            info.setCreator(builders[dto.creatorId].create(dto.name));
            info.setDisabled(dto.disabled);

            return info;
        }

        @Override
        public WorldInfoDTO toDTO(WorldInfo entity) throws SQLException {
            WorldInfoDTO dto = super.toDTO(entity);

            dto.name = entity.getName();
            dto.flag = entity.flags().get();
            dto.disabled = entity.isDisabled();

            return dto;
        }
    }
    //======================================================
    //========================_LOADER=======================
    //======================================================
}
