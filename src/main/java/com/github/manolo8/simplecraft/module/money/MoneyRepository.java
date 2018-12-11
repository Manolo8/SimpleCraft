package com.github.manolo8.simplecraft.module.money;

import com.github.manolo8.simplecraft.core.data.connection.Database;
import com.github.manolo8.simplecraft.module.user.model.identity.*;
import com.github.manolo8.simplecraft.core.data.table.Size;
import com.github.manolo8.simplecraft.module.user.identity.IdentityRepository;

import java.sql.SQLException;
import java.util.List;

public class MoneyRepository extends BaseIdentityRepository<Money,
        MoneyRepository.MoneyDTO,
        MoneyRepository.MoneyDAO,
        BaseIdentityCache<Money, ?>,
        MoneyRepository.MoneyLoader> {

    //======================================================
    //======================REPOSITORY======================
    //======================================================
    public MoneyRepository(Database database, IdentityRepository identityRepository) {
        super(database, identityRepository);
    }

    @Override
    protected MoneyDAO initDao() throws SQLException {
        return new MoneyDAO(database);
    }

    @Override
    protected MoneyLoader initLoader() {
        return new MoneyLoader(identityRepository);
    }

    @Override
    protected BaseIdentityCache initCache() {
        return new BaseIdentityCache(this);
    }

    public List<Money> findMoneyTop() throws SQLException {
        return findByIdIn(dao.findMoneyTop());
    }
    //======================================================
    //=====================_REPOSITORY======================
    //======================================================


    //======================================================
    //==========================DTO=========================
    //======================================================
    class MoneyDTO extends BaseIdentityDTO {

        @Size(value = 19, decimals = 12)
        private double coins;
        private double cash;
    }
    //======================================================
    //==========================DTO=========================
    //======================================================


    //======================================================
    //==========================DAO=========================
    //======================================================
    class MoneyDAO extends BaseIdentityDAO<MoneyDTO> {

        private final String moneyTopQuery = "SELECT id FROM MoneyUsers ORDER BY coins DESC LIMIT 100";

        protected MoneyDAO(Database database) throws SQLException {
            super(database, "MoneyUsers", MoneyDTO.class);
        }

        public List<Integer> findMoneyTop() throws SQLException {
            return fromResultListId(prepareStatement(moneyTopQuery));
        }
    }
    //======================================================
    //=========================_DAO=========================
    //======================================================

    //======================================================
    //=========================LOADER=======================
    //======================================================
    class MoneyLoader extends BaseIdentityLoader<Money, MoneyDTO> {

        public MoneyLoader(IdentityRepository identityRepository) {
            super(identityRepository);
        }

        @Override
        public Money newEntity() {
            return new Money();
        }

        @Override
        public Money fromDTO(MoneyDTO dto) throws SQLException {
            Money money = super.fromDTO(dto);

            money.setCoins(dto.coins);
            money.setCash(dto.cash);

            return money;
        }

        @Override
        public MoneyDTO toDTO(Money entity) throws SQLException {
            MoneyDTO dto = super.toDTO(entity);

            dto.coins = entity.getCoins();
            dto.cash = entity.getCash();

            return dto;
        }
    }
    //======================================================
    //========================_LOADER=======================
    //======================================================
}
