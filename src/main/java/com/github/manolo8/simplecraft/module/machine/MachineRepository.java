package com.github.manolo8.simplecraft.module.machine;

import com.github.manolo8.simplecraft.core.world.WorldInfo;
import com.github.manolo8.simplecraft.core.world.WorldInfoRepository;
import com.github.manolo8.simplecraft.core.world.container.Area;
import com.github.manolo8.simplecraft.core.data.connection.Database;
import com.github.manolo8.simplecraft.core.world.model.container.ContainerDAO;
import com.github.manolo8.simplecraft.core.world.model.container.ContainerDTO;
import com.github.manolo8.simplecraft.core.world.model.container.ContainerLoader;
import com.github.manolo8.simplecraft.core.world.model.container.ContainerRepository;
import com.github.manolo8.simplecraft.core.data.model.named.NamedCache;
import com.github.manolo8.simplecraft.module.machine.fuel.FuelRepository;
import com.github.manolo8.simplecraft.module.machine.type.MachineTypeRepository;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MachineRepository extends ContainerRepository<Machine,
        MachineRepository.MachineDTO,
        MachineRepository.MachineDAO,
        MachineRepository.MachineCache,
        MachineRepository.MachineLoader> {

    private final MachineTypeRepository machineTypeRepository;
    private final WorldInfoRepository worldInfoRepository;
    private final FuelRepository fuelRepository;

    //======================================================
    //======================REPOSITORY======================
    //======================================================
    public MachineRepository(Database database,
                             WorldInfoRepository worldInfoRepository,
                             MachineTypeRepository machineTypeRepository,
                             FuelRepository fuelRepository) {
        super(database);

        this.machineTypeRepository = machineTypeRepository;
        this.worldInfoRepository = worldInfoRepository;
        this.fuelRepository = fuelRepository;
    }

    public static ItemStack toItemStack(Machine machine) {

        ItemStack item = new ItemStack(Material.SPAWNER);

        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName("§eMachine (" + machine.type.getName() + ")");
        List<String> lore = new ArrayList<>();

        lore.add("stored " + machine.getStored());
        lore.add("type " + machine.getType().getId());

        meta.setLore(lore);
        item.setItemMeta(meta);

        machine.remove();

        return item;
    }

    @Override
    protected MachineDAO initDao() throws SQLException {
        return new MachineDAO(database);
    }

    @Override
    protected MachineLoader initLoader() {
        return new MachineLoader(worldInfoRepository);
    }

    @Override
    protected MachineCache initCache() {
        return new MachineCache(this);
    }

    public Machine create(WorldInfo info, Area area, ItemStack item) throws SQLException {

        int typeId = 0;
        int stored = 0;

        if (item.hasItemMeta()) {
            ItemMeta meta = item.getItemMeta();

            if (meta.hasLore()) {
                for (String string : meta.getLore()) {
                    if (string.startsWith("stored ")) stored = NumberUtils.toInt(string.substring(7));
                    if (string.startsWith("type ")) typeId = NumberUtils.toInt(string.substring(5));
                }
            }
        }

        MachineDTO dto = new MachineDTO();

        dto.typeId = typeId;
        dto.stored = stored;
        dto.worldId = info.getId();
        dto.load(area);

        return create(dto);
    }
    //======================================================
    //=====================_REPOSITORY======================
    //======================================================


    //======================================================
    //==========================DTO=========================
    //======================================================
    class MachineDTO extends ContainerDTO {

        private int typeId;
        private int fuelId;
        private double stored;
        private int time;
        private int direction;
        private boolean auto;

    }
    //======================================================
    //=========================_DTO=========================
    //======================================================


    //======================================================
    //==========================DAO=========================
    //======================================================
    class MachineDAO extends ContainerDAO<MachineDTO> {

        MachineDAO(Database database) throws SQLException {
            super(database, "Machines", MachineDTO.class);
        }

    }
    //======================================================
    //=========================_DAO=========================
    //======================================================

    //======================================================
    //==========================CACHE=======================
    //======================================================
    class MachineCache extends NamedCache<Machine, MachineRepository> {

        MachineCache(MachineRepository repository) {
            super(repository);
        }
    }
    //======================================================
    //=========================_CACHE=======================
    //======================================================


    //======================================================
    //=========================LOADER=======================
    //======================================================
    class MachineLoader extends ContainerLoader<Machine, MachineDTO> {

        protected MachineLoader(WorldInfoRepository worldInfoRepository) {
            super(worldInfoRepository);
        }

        @Override
        public Machine newEntity() {
            return new Machine();
        }

        @Override
        public Machine fromDTO(MachineDTO dto) throws SQLException {
            Machine entity = super.fromDTO(dto);

            entity.setType(machineTypeRepository.findOne(dto.typeId));
            entity.setFuel(fuelRepository.findOne(dto.fuelId));
            entity.setAuto(dto.auto);
            entity.setTime(dto.time);
            entity.setDirection(dto.direction);
            entity.setStored(dto.stored);

            return entity;
        }

        @Override
        public MachineDTO toDTO(Machine entity) throws SQLException {
            MachineDTO dto = super.toDTO(entity);

            dto.typeId = entity.getType().getId();
            dto.fuelId = entity.getFuel() == null ? -1 : entity.getFuel().getId();
            dto.auto = entity.isAuto();
            dto.time = entity.getTime();
            dto.direction = entity.getDirection();
            dto.stored = entity.getStored();

            return dto;
        }
    }
    //======================================================
    //========================_LOADER=======================
    //======================================================
}
