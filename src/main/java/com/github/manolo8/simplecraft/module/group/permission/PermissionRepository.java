package com.github.manolo8.simplecraft.module.group.permission;

import com.github.manolo8.simplecraft.core.data.cache.Cache;
import com.github.manolo8.simplecraft.core.data.model.base.*;
import com.github.manolo8.simplecraft.core.data.cache.SaveCache;
import com.github.manolo8.simplecraft.core.data.connection.Database;
import com.github.manolo8.simplecraft.core.data.table.OnlyInsert;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class PermissionRepository extends BaseRepository<Permission,
        PermissionRepository.PermissionDTO,
        PermissionRepository.PermissionDAO,
        PermissionRepository.PermissionCache,
        PermissionRepository.PermissionLoader> {

    //======================================================
    //======================REPOSITORY======================
    //======================================================
    public PermissionRepository(Database database) {
        super(database);
    }

    @Override
    protected PermissionDAO initDao() throws SQLException {
        return new PermissionDAO(database);
    }

    @Override
    protected PermissionLoader initLoader() {
        return new PermissionLoader();
    }

    @Override
    protected PermissionCache initCache() {
        return new PermissionCache(this);
    }

    public List<Permission> findGroupPermissions(int groupId) throws SQLException {
        synchronized (Cache.LOCKER) {
            return fromDTO(dao.findGroupPermissions(groupId));
        }
    }

    public Permission create(int id, String key, int value) throws SQLException {
        PermissionDTO dto = new PermissionDTO();

        dto.groupId = id;
        dto.key1 = key;
        dto.value = value;

        return create(dto);
    }
    //======================================================
    //=====================_REPOSITORY======================
    //======================================================


    //======================================================
    //==========================DTO=========================
    //======================================================
    class PermissionDTO extends DTO {

        @OnlyInsert
        private int groupId;
        private String key1;
        private int value;
    }
    //======================================================
    //=========================_DTO=========================
    //======================================================


    //======================================================
    //==========================DAO=========================
    //======================================================
    class PermissionDAO extends BaseDAO<PermissionDTO> {

        private final String findGroupPermissionsQuery = "SELECT * FROM GroupPermissions WHERE groupId=?";

        protected PermissionDAO(Database database) throws SQLException {
            super(database, "GroupPermissions", PermissionDTO.class);
        }

        public List<PermissionDTO> findGroupPermissions(int groupId) throws SQLException {
            PreparedStatement statement = prepareStatement(findGroupPermissionsQuery);

            statement.setInt(1, groupId);

            List<PermissionDTO> dtos = fromResultList(statement.executeQuery());

            statement.close();

            return dtos;
        }

    }
    //======================================================
    //=========================_DAO=========================
    //======================================================


    //======================================================
    //==========================CACHE=======================
    //======================================================
    class PermissionCache extends SaveCache<Permission, PermissionRepository> {

        public PermissionCache(PermissionRepository repository) {
            super(repository);
        }
    }
    //======================================================
    //=========================_CACHE=======================
    //======================================================


    //======================================================
    //=========================LOADER=======================
    //======================================================
    class PermissionLoader extends BaseLoader<Permission, PermissionDTO> {

        @Override
        public Permission newEntity() {
            return new Permission();
        }

        @Override
        public Permission fromDTO(PermissionDTO dto) throws SQLException {
            Permission permission = super.fromDTO(dto);

            permission.setValue(dto.value);
            permission.setKey(dto.key1);

            return permission;
        }

        @Override
        public PermissionDTO toDTO(Permission entity) throws SQLException {
            PermissionDTO dto = super.toDTO(entity);

            dto.key1 = entity.getKey();
            dto.value = entity.getValue();

            return dto;
        }
    }
    //======================================================
    //========================_LOADER=======================
    //======================================================
}
