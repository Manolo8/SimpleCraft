package com.github.manolo8.simplecraft.module.plot.user;

import com.github.manolo8.simplecraft.core.data.connection.Database;
import com.github.manolo8.simplecraft.module.user.model.identity.*;
import com.github.manolo8.simplecraft.module.plot.PlotRepository;
import com.github.manolo8.simplecraft.module.user.identity.IdentityRepository;
import com.github.manolo8.simplecraft.utils.entity.LazyLoaderList;

import java.sql.SQLException;

public class PlotUserRepository extends BaseIdentityRepository<PlotUser,
        BaseIdentityDTO,
        PlotUserRepository.PlotUserDAO,
        BaseIdentityCache<PlotUser, ?>,
        PlotUserRepository.PlotUserLoader> {

    private final PlotRepository plotRepository;

    //======================================================
    //======================REPOSITORY======================
    //======================================================
    public PlotUserRepository(Database database,
                              IdentityRepository identityRepository,
                              PlotRepository plotRepository) {
        super(database, identityRepository);

        this.plotRepository = plotRepository;
    }

    @Override
    protected PlotUserDAO initDao() throws SQLException {
        return new PlotUserDAO(database);
    }

    @Override
    protected PlotUserLoader initLoader() {
        return new PlotUserLoader(identityRepository);
    }

    @Override
    protected BaseIdentityCache<PlotUser, ?> initCache() {
        return new BaseIdentityCache<>(this);
    }
    //======================================================
    //=====================_REPOSITORY======================
    //======================================================


    //======================================================
    //=========================DAO==========================
    //======================================================
    class PlotUserDAO extends BaseIdentityDAO<BaseIdentityDTO> {

        protected PlotUserDAO(Database database) throws SQLException {
            super(database, "PlotUsers", BaseIdentityDTO.class);
        }
    }
    //======================================================
    //========================_DAO==========================
    //======================================================


    //======================================================
    //=======================LOADER=========================
    //======================================================
    class PlotUserLoader extends BaseIdentityLoader<PlotUser, BaseIdentityDTO> {

        public PlotUserLoader(IdentityRepository identityRepository) {
            super(identityRepository);
        }

        @Override
        public PlotUser newEntity() {
            return new PlotUser();
        }

        @Override
        public PlotUser fromDTO(BaseIdentityDTO dto) throws SQLException {
            PlotUser p = super.fromDTO(dto);

            p.setPlots(new LazyLoaderList<>(() -> plotRepository.findByIdentity(p.getIdentity())));

            return p;
        }
    }
    //======================================================
    //======================_LOADER=========================
    //======================================================
}
