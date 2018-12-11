package com.github.manolo8.simplecraft.module.tag;

import com.github.manolo8.simplecraft.module.user.User;

public class TagUser {

    protected User user;

    protected TagList tagList;
    protected TagTeam team;

    protected long groupChange;
    protected long clanChange;
    protected boolean hiddenChange;

    public TagUser(User user) {
        this.user = user;
        tagList = TagList.instance;
    }

    public void quit() {
        if (team == null) return;
        tagList.quit(this);
    }

    public boolean isChanged() {
        long group = user.group().getLastModified() + user.group().get().getLastModified();
        long clan = user.clan().getLastModified() + (user.clan().isIn() ? user.clan().get().getLastModified() : 0);
        boolean hidden = user.isHidden();

        if (group != groupChange || clan != clanChange || hidden != hiddenChange) {
            groupChange = group;
            clanChange = clan;
            hiddenChange = hidden;
            return true;
        }
        return false;
    }

    public String getPrefix() {
        return user.group().get().getTag();
    }

    public String getSuffix() {
        return hiddenChange ? "§7HIDDEN" : user.clan().isIn() ? user.clan().get().getColoredTag() : "";
    }

    public int getPriority() {
        return user.getPermissionQuantity("simplecraft.group.priority");
    }

    public void tick() {
        if (isChanged()) {
            if (team == null) tagList.join(this);
            else tagList.updateUser(this);
        }
    }
}
