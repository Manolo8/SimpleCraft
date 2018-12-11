package com.github.manolo8.simplecraft.core.placeholder.tools;

import java.util.Arrays;

public class ComplexStoredString implements StoredString {

    final StringBuilder builder;
    final Content[] contents;
    final String end;

    public ComplexStoredString(Content[] contents, String end) {
        this.builder = new StringBuilder();
        this.contents = contents;
        this.end = end;
    }

    @Override
    public long lastModified() {
        long val = 0;

        for (Content content : contents) {
            val += content.holder.lastModified();
        }

        return val;
    }

    @Override
    public String value() {
        builder.setLength(0);

        for (Content content : contents) {
            builder.append(content.before).append(content.holder.value());
        }

        return builder.append(end).toString();
    }
}
