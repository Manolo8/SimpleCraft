package com.github.manolo8.simplecraft.module.group;

import com.github.manolo8.simplecraft.core.data.model.named.NamedEntity;
import com.github.manolo8.simplecraft.module.group.permission.Permission;
import com.github.manolo8.simplecraft.module.group.permission.PermissionRepository;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class Group extends NamedEntity {

    protected PermissionRepository repository;
    private String tag;
    private HashMap<String, Permission> permissions;
    private boolean isDefault;
    private Group parent;

    //======================================================
    //=====================ENCAPSULATION====================
    //======================================================
    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
        modified();
    }

    public Set<String> getPermissions() {
        return permissions.keySet();
    }

    public void setPermissions(List<Permission> permissions) {
        this.permissions = new HashMap();

        for (Permission permission : permissions) {
            this.permissions.put(permission.getKey(), permission);
        }
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean aDefault) {
        isDefault = aDefault;
        modified();
    }

    public Group getParent() {
        return parent;
    }

    public void setParent(Group parent) {
        this.parent = parent;
    }
    //======================================================
    //====================_ENCAPSULATION====================
    //======================================================


    //======================================================
    //=======================METHODS========================
    //======================================================

    public boolean checkHierarchy(Group parent) {
        return this != parent && (this.parent == null || this.parent.checkHierarchy(parent));
    }

    public boolean changeParent(Group group) {

        modified();
        parent = group;

        return true;
    }

    public void addPermission(String key, int value) throws SQLException {
        permissions.put(key, repository.create(id, key, value));
    }

    public void removePermission(Permission permission) {
        permissions.remove(permission.getKey());

        permission.remove();
    }

    public boolean hasPermission(String key) {
        Permission permission = getPermission(key);

        if (permission != null) {
            return permission.getValue() > 0;
        } else {
            return parent != null && parent.hasPermission(key);
        }
    }

    public int getPermissionQuantity(String key) {
        Permission permission = getPermission(key);

        if (permission != null) {
            return permission.getValue();
        }

        return parent == null ? 0 : parent.getPermissionQuantity(key);
    }

    public Permission getPermission(String key) {
        return permissions.get(key);
    }

    //======================================================
    //=======================_METHODS=======================
    //======================================================


    //======================================================
    //=======================ENTITY=========================
    //======================================================
    @Override
    public void remove() {
        super.remove();

        for (Permission permission : permissions.values()) permission.remove();
    }
    //======================================================
    //======================_ENTITY=========================
    //======================================================
}
