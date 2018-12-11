package com.github.manolo8.simplecraft.module.hologram;

import com.github.manolo8.simplecraft.core.world.WorldInfoRepository;
import com.github.manolo8.simplecraft.core.data.connection.Database;
import com.github.manolo8.simplecraft.core.world.model.container.ContainerDAO;
import com.github.manolo8.simplecraft.core.world.model.container.ContainerDTO;
import com.github.manolo8.simplecraft.core.world.model.container.ContainerLoader;
import com.github.manolo8.simplecraft.core.world.model.container.ContainerRepository;
import com.github.manolo8.simplecraft.core.data.model.named.NamedCache;

import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class HologramRepository extends ContainerRepository<Hologram,
        HologramRepository.HologramDTO,
        HologramRepository.HologramDAO,
        HologramRepository.HologramCache,
        HologramRepository.HologramLoader> {

    //======================================================
    //======================REPOSITORY======================
    //======================================================
    private final WorldInfoRepository worldInfoRepository;

    public HologramRepository(Database database, WorldInfoRepository worldInfoRepository) {
        super(database);

        this.worldInfoRepository = worldInfoRepository;
    }

    @Override
    protected HologramDAO initDao() throws SQLException {
        return new HologramDAO(database);
    }

    @Override
    protected HologramLoader initLoader() {
        return new HologramLoader(worldInfoRepository);
    }

    @Override
    protected HologramCache initCache() {
        return new HologramCache(this);
    }
    //======================================================
    //=====================_REPOSITORY======================
    //======================================================


    //======================================================
    //=========================_DTO=========================
    //======================================================
    class HologramDTO extends ContainerDTO {
        public byte[] data;
    }
    //======================================================
    //==========================DTO=========================
    //======================================================


    //======================================================
    //=========================_DAO=========================
    //======================================================
    class HologramDAO extends ContainerDAO<HologramDTO> {
        HologramDAO(Database database) throws SQLException {
            super(database, "Holograms", HologramDTO.class);
        }
    }
    //======================================================
    //==========================DAO=========================
    //======================================================


    //======================================================
    //=========================_CACHE=======================
    //======================================================
    class HologramCache extends NamedCache<Hologram, HologramRepository> {

        HologramCache(HologramRepository repository) {
            super(repository);
        }
    }
    //======================================================
    //==========================CACHE=======================
    //======================================================


    //======================================================
    //=========================LOADER=======================
    //======================================================
    class HologramLoader extends ContainerLoader<Hologram, HologramDTO> {

        public HologramLoader(WorldInfoRepository worldInfoRepository) {
            super(worldInfoRepository);
        }

        @Override
        public Hologram newEntity() {
            return new Hologram();
        }

        @Override
        public Hologram fromDTO(HologramDTO dto) throws SQLException {
            Hologram entity = super.fromDTO(dto);

            byte[] bytes = dto.data;

            int r = 0;
            List<String> lines = new ArrayList<>();

            if (bytes != null) {
                while (r < bytes.length) {

                    int length = bytes[r];

                    lines.add(new String(bytes, r + 1, length, StandardCharsets.UTF_8));

                    r += length + 1;
                }
            }

            entity.setLines(lines);

            return entity;
        }

        @Override
        public HologramDTO toDTO(Hologram entity) throws SQLException {
            HologramDTO dto = super.toDTO(entity);

            List<String> lines = entity.getLines();

            if (lines.isEmpty()) {
                dto.data = new byte[0];
            } else {

                byte[][] bytes = new byte[lines.size()][];

                int total = 0;

                for (int i = 0; i < lines.size(); i++) {
                    String string = lines.get(i);

                    bytes[i] = string.getBytes(StandardCharsets.UTF_8);
                    total += bytes[i].length;

                    if (bytes[i].length > 255) {
                        dto.data = new byte[0];
                        return dto;
                    }

                }

                byte[] result = new byte[total + lines.size()];

                int index = 0;

                for (int i = 0; i < lines.size(); i++) {
                    int length = bytes[i].length;
                    result[index++] = (byte) length;

                    System.arraycopy(bytes[i], 0, result, index, length);
                    index += length;
                }

                dto.data = result;

            }

            return dto;
        }
    }
    //======================================================
    //========================_LOADER=======================
    //======================================================
}
