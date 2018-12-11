package com.github.manolo8.simplecraft.core.chat;

public class PrefixBuilder {

    private StringBuilder builder;
    private boolean clear;

    public PrefixBuilder() {
        builder = new StringBuilder();
    }

    public PrefixBuilder addValue(String tag) {
        if (!tag.isEmpty()) {
            if (clear) builder.append(" §r");
            clear = true;
            builder.append(tag);
        }

        return this;
    }

    public PrefixBuilder addValueBorder(String tag) {
        if (!tag.isEmpty()) {

            if (clear) builder.append(" §r");
            clear = true;

            builder.append("§7[§r").append(tag).append("§7]");
        }
        return this;
    }

    public String addMessageAndBuild(String message, boolean local) {
        builder.append(local ? "§e: " : "§7: ").append(message);

        String finish = builder.toString();
        builder.setLength(0);
        clear = false;

        return finish;
    }

}
