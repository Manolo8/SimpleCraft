package com.github.manolo8.simplecraft.module.group;

import com.github.manolo8.simplecraft.core.data.connection.Database;
import com.github.manolo8.simplecraft.core.data.model.named.*;
import com.github.manolo8.simplecraft.module.group.permission.PermissionRepository;
import com.github.manolo8.simplecraft.module.group.user.GroupUserRepository;
import com.github.manolo8.simplecraft.module.user.identity.IdentityRepository;

import java.lang.ref.Reference;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class GroupRepository extends NamedRepository<Group,
        GroupRepository.GroupDTO,
        GroupRepository.GroupDAO,
        GroupRepository.GroupCache,
        GroupRepository.GroupLoader> {

    private final GroupUserRepository groupUserRepository;
    private final PermissionRepository permissionRepository;

    //======================================================
    //======================REPOSITORY======================
    //======================================================
    public GroupRepository(Database database, IdentityRepository identityRepository) throws SQLException {
        super(database);

        this.permissionRepository = new PermissionRepository(database);
        this.groupUserRepository = new GroupUserRepository(database, identityRepository, this);
    }

    @Override
    public void init() throws SQLException {
        super.init();
        permissionRepository.init();
        groupUserRepository.init();
    }

    @Override
    protected GroupDAO initDao() throws SQLException {
        return new GroupDAO(database);
    }

    @Override
    protected GroupLoader initLoader() {
        return new GroupLoader();
    }

    @Override
    protected GroupCache initCache() {
        return new GroupCache(this);
    }

    public Group findDefault() throws SQLException {
        Group group = cache.getIfHasDefault();

        if (group != null) return group;

        group = fromDTO(dao.findDefault());

        if (group != null) return group;

        return create("DEFAULT", "", true);
    }

    public Group create(String name, String tag, boolean def) throws SQLException {
        GroupDTO dto = new GroupDTO();

        dto.name = name;
        dto.fastName = name.toLowerCase();

        dto.tag = tag;
        dto.isDefault = def;

        return create(dto);
    }

    public Group findOneOrDefault(int groupId) throws SQLException {
        Group group = findOne(groupId);

        return group == null ? findDefault() : group;
    }

    @Override
    public void delete(Group entity) throws SQLException {
        super.delete(entity);

        //Remove os parentes
        cache.removeParent(entity);

        //Remove os jogadores no grupo e coloca no default
        groupUserRepository.getCache().replaceGroupRemoved(entity, findDefault());
    }

    public PermissionRepository getPermissionRepository() {
        return permissionRepository;
    }

    public GroupUserRepository getGroupUserRepository() {
        return groupUserRepository;
    }
    //======================================================
    //=====================_REPOSITORY======================
    //======================================================


    //======================================================
    //==========================DTO=========================
    //======================================================
    class GroupDTO extends NamedDTO {

        private String tag;
        private int parentId;
        private boolean isDefault;
    }
    //======================================================
    //=========================_DTO=========================
    //======================================================


    //======================================================
    //==========================DAO=========================
    //======================================================
    class GroupDAO extends NamedDAO<GroupDTO> {

        private final String findDefaultQuery = "SELECT * FROM Groups WHERE isDefault=1";

        protected GroupDAO(Database database) throws SQLException {
            super(database, "Groups", GroupDTO.class);
        }

        public GroupDTO findDefault() throws SQLException {
            Statement statement = createStatement();

            ResultSet result = statement.executeQuery(findDefaultQuery);

            GroupDTO dto = result.next() ? fromResult(result) : null;

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
    class GroupCache extends NamedCache<Group, GroupRepository> {

        public GroupCache(GroupRepository repository) {
            super(repository);
        }

        public Group getIfHasDefault() {
            for (Reference<Group> reference : references) {
                Group group = extract(reference);

                if (group != null && group.isDefault()) {
                    return group;
                }

            }

            return null;
        }

        public void removeParent(Group parent) {
            for (Reference<Group> reference : references) {
                Group group = extract(reference);

                if (group != null && group.getParent() == parent) {
                    group.changeParent(null);
                }

            }
        }
    }
    //======================================================
    //=========================_CACHE=======================
    //======================================================

    //======================================================
    //=========================LOADER=======================
    //======================================================
    class GroupLoader extends NamedLoader<Group, GroupDTO> {

        @Override
        public Group newEntity() {
            return new Group();
        }

        @Override
        public Group fromDTO(GroupDTO dto) throws SQLException {
            Group group = super.fromDTO(dto);

            group.setTag(dto.tag);
            group.setPermissions(permissionRepository.findGroupPermissions(dto.id));
            group.setParent(dto.parentId == 0 ? null : findOne(dto.parentId));
            group.setDefault(dto.isDefault);

            group.repository = permissionRepository;

            return group;
        }

        @Override
        public GroupDTO toDTO(Group entity) throws SQLException {
            GroupDTO dto = super.toDTO(entity);

            dto.tag = entity.getTag();
            dto.parentId = entity.getParent() == null ? 0 : entity.getParent().getId();
            dto.isDefault = entity.isDefault();

            return dto;
        }
    }
    //======================================================
    //========================_LOADER=======================
    //======================================================
}
