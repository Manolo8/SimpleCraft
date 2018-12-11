package com.github.manolo8.simplecraft.module.group.user;

import com.github.manolo8.simplecraft.module.user.model.identity.BaseIdentity;
import com.github.manolo8.simplecraft.module.group.Group;
import com.github.manolo8.simplecraft.module.group.GroupRepository;
import com.github.manolo8.simplecraft.module.user.MessageType;

import java.sql.SQLException;

public class GroupUser extends BaseIdentity {

    private GroupRepository repository;
    private long expiration;
    private Group group;

    public GroupUser(GroupRepository repository) {
        this.repository = repository;
    }

    //======================================================
    //=====================ENCAPSULATION====================
    //======================================================
    public Group get() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public long getExpiration() {
        return expiration;
    }

    public void setExpiration(long expiration) {
        this.expiration = expiration;
    }
    //======================================================
    //====================_ENCAPSULATION====================
    //======================================================


    //======================================================
    //========================METHODS=======================
    //======================================================
    public void changeGroup(Group group, long time) {
        if (group == this.group) {
            this.expiration = time == 0 || this.expiration == 0 ? 0 : this.expiration + time;
        } else {
            this.group = group;

            this.expiration = time == 0 ? 0 : System.currentTimeMillis() + time;
            modified();
        }
    }

    public void tick() {
        if (expiration != 0 && System.currentTimeMillis() > expiration) {

            identity.user().sendMessage(MessageType.INFO, "O seu " + group.getName() + " acabou de expirar!");

            try {
                changeGroup(repository.findDefault(), 0);
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }
    }
    //======================================================
    //=======================_METHODS=======================
    //======================================================
}
