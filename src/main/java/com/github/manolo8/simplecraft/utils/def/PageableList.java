package com.github.manolo8.simplecraft.utils.def;

import java.util.List;

public class PageableList<T> {

    private List<T> items;
    private String title;
    private int page;
    private int size;

    public PageableList(List<T> items, String title, int page, int size) {
        this.items = items;
        this.title = title;
        this.size = size;
        this.page = page <= 0 ? 0 : page - 1;
    }

    public String build(Page<T> consumer) {
        StringBuilder builder = new StringBuilder();

        int total = items.size();
        int totalPages = total / size;
        if (total % size > 0) totalPages++;
        if (page > totalPages - 1) page = totalPages - 1;

        builder.append("\n").append(title).append(" (").append(page + 1).append("/").append(totalPages).append(")\n \n");
        int begin = size * page;

        for (int i = 0; i < size && (begin + i) < total; i++)
            consumer.build(items.get(begin + i), builder, (begin + i + 1));

        return builder.toString();
    }

    public interface Page<T> {
        void build(T item, StringBuilder builder, int current);
    }
}
