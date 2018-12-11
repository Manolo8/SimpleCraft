package com.github.manolo8.simplecraft.module.tag;

import java.util.ArrayList;
import java.util.List;

public class TagTeam {

    protected static int count;
    protected final String name;
    protected final String key;
    protected final String prefix;
    protected final String suffix;
    protected final List<TagUser> users;
    protected final List<String> members;
    protected boolean isNew;

    public TagTeam(String prefix, String suffix, int priority) {

        this.prefix = prefix;
        this.suffix = suffix;
        this.key = prefix + suffix;
        this.name = ((char) priority) + String.valueOf(++count);

        this.isNew = true;

        this.members = new ArrayList<>();
        this.users = new ArrayList<>();
    }

    public String key() {
        return key;
    }

    public void join(TagUser user) {
        users.add(user);
        members.add(user.user.identity().getName());
    }

    public void exit(TagUser user) {
        users.remove(user);
        members.remove(user.user.identity().getName());
    }
}
