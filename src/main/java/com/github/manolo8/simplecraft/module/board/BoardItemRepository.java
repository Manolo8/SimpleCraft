package com.github.manolo8.simplecraft.module.board;

import com.github.manolo8.simplecraft.core.data.cache.SaveCache;
import com.github.manolo8.simplecraft.core.data.connection.Database;
import com.github.manolo8.simplecraft.core.data.model.base.BaseDAO;
import com.github.manolo8.simplecraft.core.data.model.base.BaseLoader;
import com.github.manolo8.simplecraft.core.data.model.base.BaseRepository;
import com.github.manolo8.simplecraft.core.data.model.base.DTO;

import java.sql.SQLException;

public class BoardItemRepository extends BaseRepository<BoardItem,
        BoardItemRepository.BoardItemDTO,
        BoardItemRepository.BoardItemDAO,
        BoardItemRepository.BoardItemCache,
        BoardItemRepository.BoardItemLoader> {

    //======================================================
    //======================REPOSITORY======================
    //======================================================
    public BoardItemRepository(Database database) {
        super(database);
    }

    @Override
    protected BoardItemDAO initDao() throws SQLException {
        return new BoardItemDAO(database);
    }

    @Override
    protected BoardItemLoader initLoader() {
        return new BoardItemLoader();
    }

    @Override
    protected BoardItemCache initCache() {
        return new BoardItemCache(this);
    }

    public BoardItem create(String name, int priority) throws SQLException {
        BoardItemDTO dto = new BoardItemDTO();

        dto.value = name;
        dto.priority = priority;

        return create(dto);
    }
    //======================================================
    //=====================_REPOSITORY======================
    //======================================================


    //======================================================
    //==========================DTO=========================
    //======================================================
    class BoardItemDTO extends DTO {

        private int priority;
        private String value;

    }
    //======================================================
    //=========================_DTO=========================
    //======================================================


    //======================================================
    //==========================DAO=========================
    //======================================================
    class BoardItemDAO extends BaseDAO<BoardItemDTO> {

        BoardItemDAO(Database database) throws SQLException {
            super(database, "BoardItems", BoardItemDTO.class);
        }
    }
    //======================================================
    //=========================_DAO=========================
    //======================================================

    //======================================================
    //==========================CACHE=======================
    //======================================================
    class BoardItemCache extends SaveCache<BoardItem, BoardItemRepository> {

        BoardItemCache(BoardItemRepository repository) {
            super(repository);
        }
    }
    //======================================================
    //=========================_CACHE=======================
    //======================================================


    //======================================================
    //=========================LOADER=======================
    //======================================================
    class BoardItemLoader extends BaseLoader<BoardItem, BoardItemDTO> {

        @Override
        public BoardItem newEntity() {
            return new BoardItem();
        }

        @Override
        public BoardItem fromDTO(BoardItemDTO dto) throws SQLException {
            BoardItem entity = super.fromDTO(dto);

            entity.priority = dto.priority;
            entity.value = dto.value;

            return entity;
        }

        @Override
        public BoardItemDTO toDTO(BoardItem entity) throws SQLException {
            BoardItemDTO dto = super.toDTO(entity);

            dto.priority = entity.priority;
            dto.value = entity.value;

            return dto;
        }
    }
    //======================================================
    //========================_LOADER=======================
    //======================================================
}
