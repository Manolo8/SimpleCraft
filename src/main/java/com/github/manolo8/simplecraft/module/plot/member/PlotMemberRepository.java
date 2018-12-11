package com.github.manolo8.simplecraft.module.plot.member;

import com.github.manolo8.simplecraft.core.data.connection.Database;
import com.github.manolo8.simplecraft.module.user.model.identity.*;
import com.github.manolo8.simplecraft.core.data.table.OnlyInsert;
import com.github.manolo8.simplecraft.core.data.table.Size;
import com.github.manolo8.simplecraft.module.plot.Plot;
import com.github.manolo8.simplecraft.module.user.identity.Identity;
import com.github.manolo8.simplecraft.module.user.identity.IdentityRepository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class PlotMemberRepository extends BaseIdentityRepository<PlotMember,
        PlotMemberRepository.PlotMemberDTO,
        PlotMemberRepository.PlotMemberDAO,
        BaseIdentityCache<PlotMember, ?>,
        PlotMemberRepository.PlotMemberLoader> {


    //======================================================
    //======================REPOSITORY======================
    //======================================================
    public PlotMemberRepository(Database database, IdentityRepository identityRepository) {
        super(database, identityRepository);
    }

    @Override
    protected PlotMemberDAO initDao() throws SQLException {
        return new PlotMemberDAO(database);
    }

    @Override
    protected PlotMemberLoader initLoader() {
        return new PlotMemberLoader(identityRepository);
    }

    @Override
    protected BaseIdentityCache initCache() {
        return new BaseIdentityCache(this);
    }

    public List<PlotMember> findByPlotId(int plotId) throws SQLException {
        return findByIdIn(dao.findByPlotId(plotId));
    }

    public PlotMember create(Plot plot, Identity identity) throws SQLException {
        PlotMemberDTO dto = new PlotMemberDTO();

        dto.plotId = plot.getId();
        dto.setIdentityId(identity.getId());

        return create(dto);
    }
    //======================================================
    //=====================_REPOSITORY======================
    //======================================================


    //======================================================
    //==========================DTO=========================
    //======================================================
    class PlotMemberDTO extends BaseIdentityDTO {

        @OnlyInsert
        private int plotId;

        @Size(1)
        private byte[] flag;
    }
    //======================================================
    //=========================_DTO=========================
    //======================================================


    //======================================================
    //==========================DAO=========================
    //======================================================
    class PlotMemberDAO extends BaseIdentityDAO<PlotMemberDTO> {

        private final String findByPlotIdQuery = "SELECT id FROM PlotMembers WHERE plotId=?";

        protected PlotMemberDAO(Database database) throws SQLException {
            super(database, "PlotMembers", PlotMemberDTO.class);
        }

        public List<Integer> findByPlotId(int plotId) throws SQLException {
            PreparedStatement statement = prepareStatement(findByPlotIdQuery);

            statement.setInt(1, plotId);

            return fromResultListId(statement);
        }
    }
    //======================================================
    //=========================_DAO=========================
    //======================================================


    //======================================================
    //==========================LOADER======================
    //======================================================
    class PlotMemberLoader extends BaseIdentityLoader<PlotMember, PlotMemberDTO> {

        public PlotMemberLoader(IdentityRepository identityRepository) {
            super(identityRepository);
        }

        @Override
        public PlotMember newEntity() {
            return new PlotMember();
        }

        @Override
        public PlotMember fromDTO(PlotMemberDTO dto) throws SQLException {
            PlotMember member = super.fromDTO(dto);

            member.setFlags(new MemberFlag(dto.flag));

            return member;
        }

        @Override
        public PlotMemberDTO toDTO(PlotMember entity) throws SQLException {
            PlotMemberDTO dto = super.toDTO(entity);

            dto.flag = entity.flags().get();

            return dto;
        }
    }
    //======================================================
    //=========================_LOADER======================
    //======================================================

}
