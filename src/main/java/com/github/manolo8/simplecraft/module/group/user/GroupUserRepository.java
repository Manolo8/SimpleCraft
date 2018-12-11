package com.github.manolo8.simplecraft.module.group.user;

import com.github.manolo8.simplecraft.core.data.connection.Database;
import com.github.manolo8.simplecraft.module.user.model.identity.*;
import com.github.manolo8.simplecraft.module.group.Group;
import com.github.manolo8.simplecraft.module.group.GroupRepository;
import com.github.manolo8.simplecraft.module.user.identity.IdentityRepository;

import java.lang.ref.Reference;
import java.sql.SQLException;

public class GroupUserRepository extends BaseIdentityRepository<GroupUser,
        GroupUserRepository.GroupUserDTO,
        GroupUserRepository.GroupUserDAO,
        GroupUserRepository.GroupUserCache,
        GroupUserRepository.GroupUserLoader> {

    private final GroupRepository groupRepository;

    //======================================================
    //======================REPOSITORY======================
    //======================================================
    public GroupUserRepository(Database database,
                               IdentityRepository identityRepository,
                               GroupRepository groupRepository) {
        super(database, identityRepository);

        this.groupRepository = groupRepository;
    }

    @Override
    protected GroupUserDAO initDao() throws SQLException {
        return new GroupUserDAO(database);
    }

    @Override
    protected GroupUserLoader initLoader() {
        return new GroupUserLoader(identityRepository);
    }

    @Override
    protected GroupUserCache initCache() {
        return new GroupUserCache(this);
    }
    //======================================================
    //=====================_REPOSITORY======================
    //======================================================


    //======================================================
    //==========================DTO=========================
    //======================================================
    class GroupUserDTO extends BaseIdentityDTO {

        private int groupId;
        private long expiration;

    }
    //======================================================
    //=========================_DTO=========================
    //======================================================


    //======================================================
    //==========================DAO=========================
    //======================================================
    class GroupUserDAO extends BaseIdentityDAO<GroupUserDTO> {

        GroupUserDAO(Database database) throws SQLException {
            super(database, "GroupUsers", GroupUserDTO.class);
        }
    }
    //======================================================
    //=========================_DAO=========================
    //======================================================

    //======================================================
    //==========================CACHE=======================
    //======================================================
    public class GroupUserCache extends BaseIdentityCache<GroupUser, GroupUserRepository> {

        GroupUserCache(GroupUserRepository repository) {
            super(repository);
        }

        public void replaceGroupRemoved(Group removed, Group def) {
            for (Reference<GroupUser> reference : references) {

                GroupUser groupUser = extract(reference);

                if (groupUser != null && groupUser.get() == removed) {
                    groupUser.changeGroup(def, 0);
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
    class GroupUserLoader extends BaseIdentityLoader<GroupUser, GroupUserDTO> {

        public GroupUserLoader(IdentityRepository identityRepository) {
            super(identityRepository);
        }

        @Override
        public GroupUser newEntity() {
            return new GroupUser(groupRepository);
        }

        @Override
        public GroupUser fromDTO(GroupUserDTO dto) throws SQLException {
            GroupUser entity = super.fromDTO(dto);

            entity.setGroup(groupRepository.findOneOrDefault(dto.groupId));
            entity.setExpiration(dto.expiration);

            return entity;
        }

        @Override
        public GroupUserDTO toDTO(GroupUser entity) throws SQLException {
            GroupUserDTO dto = super.toDTO(entity);

            dto.groupId = entity.get().getId();
            dto.expiration = entity.getExpiration();

            return dto;
        }
    }
    //======================================================
    //========================_LOADER=======================
    //======================================================
}
