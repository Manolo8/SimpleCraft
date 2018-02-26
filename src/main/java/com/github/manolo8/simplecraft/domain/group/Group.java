package com.github.manolo8.simplecraft.domain.group;

import com.github.manolo8.simplecraft.model.BaseEntity;
import com.github.manolo8.simplecraft.model.NamedEntity;

import java.util.List;

public class Group extends NamedEntity {

    private String tag;
    private Group parent;
    private List<String> permissions;
    private boolean isDefault;

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public Group getParent() {
        return parent;
    }

    public void setParent(Group parent) {
        this.parent = parent;
    }

    public List<String> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<String> permissions) {
        this.permissions = permissions;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean aDefault) {
        isDefault = aDefault;
    }

    public boolean hasPermission(String permission) {
        for (String str : permissions)
            if (permission.equals(str)) return true;

        return parent != null && parent.hasPermission(permission);
    }

    public boolean addPermission(String permission) {
        if(hasPermission(permission)) return false;
        getPermissions().add(permission);
        return true;
    }
}
