package com.github.manolo8.simplecraft.module.rank;

import com.github.manolo8.simplecraft.core.data.connection.Database;
import com.github.manolo8.simplecraft.core.data.model.named.*;
import com.github.manolo8.simplecraft.core.data.table.Size;
import org.bukkit.Material;

import java.lang.ref.Reference;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class RankRepository extends NamedRepository<Rank,
        RankRepository.RankDTO,
        RankRepository.RankDAO,
        RankRepository.RankCache,
        RankRepository.RankLoader> {

    //======================================================
    //======================REPOSITORY======================
    //======================================================
    public RankRepository(Database database) {
        super(database);
    }

    @Override
    protected RankDAO initDao() throws SQLException {
        return new RankDAO(database);
    }

    @Override
    protected RankLoader initLoader() {
        return new RankLoader();
    }

    @Override
    protected RankCache initCache() {
        return new RankCache(this);
    }

    public Rank findDefault() throws SQLException {
        Rank rank = cache.getIfHasDefault();

        if (rank != null) return rank;

        rank = fromDTO(dao.findDefault());

        if (rank != null) return rank;

        rank = create("l5");

        rank.setRank(1);
        rank.setTag("§7Lápis 5");

        return rank;
    }

    public Rank findOneOrDefault(int groupId) throws SQLException {
        Rank rank = findOne(groupId);

        return rank == null ? findDefault() : rank;
    }
    //======================================================
    //=====================_REPOSITORY======================
    //======================================================


    //======================================================
    //==========================DTO=========================
    //======================================================
    class RankDTO extends NamedDTO {

        private int rank;
        @Size(value = 16, decimals = 3)
        private double cost;
        private String tag;
        private int representationId;
    }
    //======================================================
    //=========================_DTO=========================
    //======================================================


    //======================================================
    //==========================DAO=========================
    //======================================================
    class RankDAO extends NamedDAO<RankDTO> {

        private final String findDefaultQuery = "SELECT * FROM Ranks WHERE rank=1";

        RankDAO(Database database) throws SQLException {
            super(database, "Ranks", RankDTO.class);
        }

        public RankDTO findDefault() throws SQLException {

            Statement statement = createStatement();

            ResultSet result = statement.executeQuery(findDefaultQuery);

            RankDTO dto = result.next() ? fromResult(result) : null;

            statement.close();

            return dto;
        }
    }
    //======================================================
    //=========================_DAO=========================
    //======================================================

    //======================================================
    //==========================CACHE=======================
    //======================================================
    public class RankCache extends NamedCache<Rank, RankRepository> {

        RankCache(RankRepository repository) {
            super(repository);
        }


        public Rank getIfHasDefault() {
            for (Reference<Rank> reference : references) {
                Rank rank = extract(reference);

                if (rank != null && rank.get() == 1) {
                    return rank;
                }
            }

            return null;
        }
    }
    //======================================================
    //=========================_CACHE=======================
    //======================================================


    //======================================================
    //=========================LOADER=======================
    //======================================================
    class RankLoader extends NamedLoader<Rank, RankDTO> {

        @Override
        public Rank newEntity() {
            return new Rank();
        }

        @Override
        public Rank fromDTO(RankDTO dto) throws SQLException {
            Rank entity = super.fromDTO(dto);

            entity.setCost(dto.cost);
            entity.setRank(dto.rank);
            entity.setRepresentation(Material.STONE);
            entity.setTag(dto.tag);

            return entity;
        }

        @Override
        public RankDTO toDTO(Rank entity) throws SQLException {
            RankDTO dto = super.toDTO(entity);

            dto.cost = entity.getCost();
            dto.rank = entity.get();
            dto.representationId = entity.getRepresentation().getId();
            dto.tag = entity.getTag();

            return dto;
        }
    }
    //======================================================
    //========================_LOADER=======================
    //======================================================
}
