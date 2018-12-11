package com.github.manolo8.simplecraft.core.commands.line;

import java.sql.SQLException;
import java.util.List;

public class TabArguments {

    private final CommandSection section;
    private final Sender sender;
    private final String[] args;
    private ParameterBuilder builder;
    private List<String> list;
    private String complete;

    public TabArguments(CommandSection section, Sender sender, String[] args, List<String> list, String complete) {
        this.section = section;
        this.sender = sender;
        this.args = args;
        this.list = list;
        this.complete = complete;
    }

    public boolean offer(String value) {
        if (value == null) return false;

        value = value.toLowerCase();
        if (value.startsWith(complete)) {
            list.add(value);
            return true;
        }
        return false;
    }

    public void offerSafe(List<String> values) {
        list.addAll(values);
    }

    public void offerSafe(String value) {
        list.add(value);
    }

    public String getComplete() {
        return complete;
    }

    public List<String> getList() {
        return list;
    }

    public String[] getArgs() {
        return args;
    }

    public Sender sender() {
        return sender;
    }

    public ParameterBuilder parameters() {
        if (builder != null) return builder;

        try {
            builder = section.createParameters(sender, args);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return builder;
    }
}
