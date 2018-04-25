package com.github.manolo8.simplecraft.modules.group;

import com.github.manolo8.simplecraft.data.model.NamedEntity;

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
        if (this.tag != null && !this.tag.equals(tag)) setNeedSave(true);
        this.tag = tag;
    }

    public Group getParent() {
        return parent;
    }

    public void setParent(Group parent) {
        if (this.parent != null && !this.parent.equals(parent)) setNeedSave(true);
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
        if (this.isDefault != aDefault) setNeedSave(true);
        isDefault = aDefault;
    }

    public boolean hasPermission(String permission) {
        for (String str : permissions)
            if (permission.equals(str)) return true;

        return parent != null && parent.hasPermission(permission);
    }

    public int getPermissionQuantity(String key) {
        String found = null;

        for (String str : permissions)
            if (str.startsWith(key)) {
                found = str;
                break;
            }


        if (found == null) return 0;

        found = found.substring(key.length());

        try {
            return Integer.parseInt(found);
        } catch (Exception e) {
            return 0;
        }
    }

    public boolean addPermission(String permission) {
        if (hasPermission(permission)) return false;
        getPermissions().add(permission);
        setNeedSave(true);
        return true;
    }
}
