package com.github.manolo8.simplecraft.module.plot;

import com.github.manolo8.simplecraft.core.world.WorldInfoRepository;
import com.github.manolo8.simplecraft.core.world.container.Area;
import com.github.manolo8.simplecraft.core.data.connection.Database;
import com.github.manolo8.simplecraft.core.world.model.container.ContainerDAO;
import com.github.manolo8.simplecraft.core.world.model.container.ContainerDTO;
import com.github.manolo8.simplecraft.core.world.model.container.ContainerLoader;
import com.github.manolo8.simplecraft.core.world.model.container.ContainerRepository;
import com.github.manolo8.simplecraft.core.data.model.named.NamedCache;
import com.github.manolo8.simplecraft.core.data.table.OnlyInsert;
import com.github.manolo8.simplecraft.core.data.table.Size;
import com.github.manolo8.simplecraft.module.plot.member.PlotMemberRepository;
import com.github.manolo8.simplecraft.module.plot.user.PlotUserRepository;
import com.github.manolo8.simplecraft.module.user.User;
import com.github.manolo8.simplecraft.module.user.identity.Identity;
import com.github.manolo8.simplecraft.module.user.identity.IdentityRepository;
import com.github.manolo8.simplecraft.utils.def.PosXY;
import com.github.manolo8.simplecraft.utils.entity.LazyLoaderList;
import com.github.manolo8.simplecraft.utils.mc.ChunkIDGenerator;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class PlotRepository extends ContainerRepository<Plot,
        PlotRepository.PlotDTO,
        PlotRepository.PlotDAO,
        PlotRepository.PlotCache,
        PlotRepository.PlotLoader> {

    private final WorldInfoRepository worldInfoRepository;
    private IdentityRepository identityRepository;
    private PlotUserRepository plotUserRepository;
    private PlotMemberRepository plotMemberRepository;

    //======================================================
    //======================REPOSITORY======================
    //======================================================
    public PlotRepository(Database database,
                          WorldInfoRepository worldInfoRepository,
                          IdentityRepository identityRepository) throws SQLException {
        super(database);

        this.worldInfoRepository = worldInfoRepository;
        this.identityRepository = identityRepository;
        this.plotUserRepository = new PlotUserRepository(database,
                identityRepository,
                this);
        this.plotMemberRepository = new PlotMemberRepository(database,
                identityRepository);
    }

    @Override
    public void init() throws SQLException {
        super.init();
        this.plotMemberRepository.init();
        this.plotUserRepository.init();
    }

    @Override
    protected PlotDAO initDao() throws SQLException {
        return new PlotDAO(database);
    }

    @Override
    protected PlotLoader initLoader() {
        return new PlotLoader();
    }

    @Override
    protected PlotCache initCache() {
        return new PlotCache(this);
    }

    public Plot create(User owner, String name, int worldId, int x, int z) throws SQLException {
        PlotDTO dto = new PlotDTO();

        int pos = ChunkIDGenerator.generate(x, z);

        if (pos != -1 && dao.isAvailable(worldId, pos)) {

            if (name == null) {
                name = "terreno" + pos;
            }

            dto.identityId = owner.getId();
            dto.worldId = worldId;
            dto.pos = pos;
            dto.name = name;
            dto.fastName = name.toLowerCase();
            dto.flag = new byte[]{2};
            dto.load(new Area(6, x, z).add(0, 0, 0, 16, 0, 16));

            Plot plot = super.create(dto);

            owner.plot().add(plot);

            return plot;

        } else {
            return null;
        }
    }

    public Plot create(User owner, String name, int worldId) throws SQLException {
        PlotDTO dto = new PlotDTO();

        int pos = dao.findNextAvailable(worldId);

        if (pos != -1) {

            if (name == null) {
                name = "terreno" + pos;
            }

            dto.identityId = owner.getId();
            dto.worldId = worldId;
            dto.pos = pos;
            dto.name = name;
            dto.fastName = name.toLowerCase();
            dto.flag = new byte[]{2};
            PosXY xy = ChunkIDGenerator.generate(pos);
            dto.load(new Area(6, xy.getX(), xy.getZ()).add(0, 0, 0, 16, 0, 16));

            Plot plot = super.create(dto);

            owner.plot().add(plot);

            return plot;

        } else {
            return null;
        }
    }

    public List<Plot> findByIdentity(Identity identity) throws SQLException {
        return findByIdIn(dao.findByIdentityId(identity.getId()));
    }

    public PlotUserRepository getPlotUserRepository() {
        return plotUserRepository;
    }

    public PlotMemberRepository getPlotMemberRepository() {
        return plotMemberRepository;
    }
    //======================================================
    //=====================_REPOSITORY======================
    //======================================================


    //======================================================
    //==========================DTO=========================
    //======================================================
    class PlotDTO extends ContainerDTO {

        private int identityId;
        @Size(1)
        private byte[] flag;
        //baseado no X e Y (Usado para
        //Gerar um novo mais proximo)
        @OnlyInsert
        private int pos;
    }
    //======================================================
    //=========================_DTO=========================
    //======================================================


    //======================================================
    //==========================DAO=========================
    //======================================================
    class PlotDAO extends ContainerDAO<PlotDTO> {

        private final String findNextAvailableQuery = "SELECT pos+1 FROM Plots WHERE worldId=? AND pos+1 NOT IN (SELECT pos FROM Plots WHERE worldId=?) LIMIT 1;";
        private final String findByIdentityIdQuery = "SELECT id FROM Plots WHERE identityId=?;";
        private final String isAvailableQuery = "SELECT pos FROM Plots WHERE worldId=? AND pos=?;";

        protected PlotDAO(Database database) throws SQLException {
            super(database, "Plots", PlotDTO.class);
        }

        protected int findNextAvailable(int worldId) throws SQLException {
            PreparedStatement statement = prepareStatement(findNextAvailableQuery);

            statement.setInt(1, worldId);
            statement.setInt(2, worldId);

            ResultSet result = statement.executeQuery();

            int pos = 0;

            if (result.next()) pos = result.getInt(1);

            statement.close();

            return pos;
        }

        public List<Integer> findByIdentityId(int id) throws SQLException {
            PreparedStatement statement = prepareStatement(findByIdentityIdQuery);

            statement.setInt(1, id);

            return fromResultListId(statement);
        }

        public boolean isAvailable(int worldId, int pos) throws SQLException {
            PreparedStatement statement = prepareStatement(isAvailableQuery);

            statement.setInt(1, worldId);
            statement.setInt(2, pos);

            ResultSet result = statement.executeQuery();

            boolean available = !result.next();

            statement.close();

            return available;
        }
    }
    //======================================================
    //=========================_DAO=========================
    //======================================================


    //======================================================
    //==========================CACHE=======================
    //======================================================
    class PlotCache extends NamedCache<Plot, PlotRepository> {

        public PlotCache(PlotRepository repository) {
            super(repository);
        }

    }
    //======================================================
    //=========================_CACHE=======================
    //======================================================


    //======================================================
    //=========================LOADER=======================
    //======================================================
    class PlotLoader extends ContainerLoader<Plot, PlotDTO> {

        PlotLoader() {
            super(worldInfoRepository);
        }

        @Override
        public Plot newEntity() {
            return new Plot(plotMemberRepository);
        }

        @Override
        public Plot fromDTO(PlotDTO dto) throws SQLException {
            Plot plot = super.fromDTO(dto);

            plot.setIdentity(identityRepository.findOne(dto.identityId));
            plot.setMembers(new LazyLoaderList(() -> plotMemberRepository.findByPlotId(dto.id)));
            plot.setPlotFlag(new PlotFlag(dto.flag));

            return plot;
        }

        @Override
        public PlotDTO toDTO(Plot entity) throws SQLException {
            PlotDTO dto = super.toDTO(entity);

            dto.identityId = entity.getIdentity().getId();
            dto.flag = entity.getPlotFlag().get();

            return dto;
        }
    }
    //======================================================
    //========================_LOADER=======================
    //======================================================
}
