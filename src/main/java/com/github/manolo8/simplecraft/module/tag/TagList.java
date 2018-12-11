package com.github.manolo8.simplecraft.module.tag;

import com.github.manolo8.simplecraft.module.tag.packet.PacketTag;

import java.util.*;

public class TagList {

    protected static TagList instance;
    private HashMap<String, TagTeam> teams;
    private PacketTag packetTag;

    public TagList() {
        this.teams = new HashMap<>();
        this.packetTag = new PacketTag();
        instance = this;
    }

    private TagTeam getOrCreate(String prefix, String suffix, int priority) {
        prefix = prefix.isEmpty() ? prefix : prefix + " §r";
        suffix = suffix.isEmpty() ? suffix : "§r " + suffix;

        if (prefix.length() > 16) prefix = prefix.substring(0, 16);
        if (suffix.length() > 16) suffix = suffix.substring(0, 16);

        String key = prefix + suffix;

        TagTeam team = teams.get(key);

        if (team != null) {
            return team;
        } else {
            team = new TagTeam(prefix, suffix, priority);

            teams.put(key, team);

            return team;
        }
    }

    private boolean checkSame(TagUser tagUser) {
        String prefix = tagUser.getPrefix();
        String suffix = tagUser.getSuffix();

        String key = tagUser.team.key;

        return key.length() == prefix.length() + suffix.length() && key.startsWith(prefix) && key.endsWith(suffix);
    }

    public void join(TagUser tagged) {

        for (TagTeam team : teams.values()) {
            packetTag.create(team.name, team.prefix, team.suffix, 0, team.members).send(tagged.user);
        }

        findTag(tagged);
    }

    public void quit(TagUser tagged) {
        exitFromTag(tagged);
    }

    public void removeAll() {
        for (TagTeam team : teams.values()) {
            removeTeamPackets(team);
        }
    }

    public void findTag(TagUser tagged) {
        TagTeam team = getOrCreate(tagged.getPrefix(), tagged.getSuffix(), tagged.getPriority());

        team.join(tagged);

        if (team.isNew) {
            team.isNew = false;
            addTeamPackets(team);
        } else {
            addPlayerToTeamPackets(team, tagged.user.identity().getName());
        }

        tagged.team = team;
    }

    public void updateUser(TagUser tagged) {
        if (!checkSame(tagged)) {
            exitFromTag(tagged);
            findTag(tagged);
        }
    }

    public void exitFromTag(TagUser tagged) {
        TagTeam team = tagged.team;

        team.exit(tagged);

        removePlayerFromTeamPackets(team, tagged.user.identity().getName());

        if (team.users.isEmpty()) {
            teams.remove(team.key());
            removeTeamPackets(team);
        }
    }

    private void removeTeamPackets(TagTeam team) {
        packetTag.create(team.name, team.prefix, team.suffix, 1, new ArrayList<>()).sendAll();
    }

    private void removePlayerFromTeamPackets(TagTeam team, String... players) {
        removePlayerFromTeamPackets(team, Arrays.asList(players));
    }

    private void removePlayerFromTeamPackets(TagTeam team, List<String> players) {
        packetTag.create(team.name, 4, players).sendAll();
    }

    private void addTeamPackets(TagTeam team) {
        packetTag.create(team.name, team.prefix, team.suffix, 0, team.members).sendAll();
    }

    private void addPlayerToTeamPackets(TagTeam team, String player) {
        packetTag.create(team.name, 3, Collections.singletonList(player)).sendAll();
    }
}
