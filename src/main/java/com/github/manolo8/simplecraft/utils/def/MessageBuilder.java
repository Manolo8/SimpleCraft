package com.github.manolo8.simplecraft.utils.def;

import com.github.manolo8.simplecraft.utils.calculator.FontWidthCalculator;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

public class MessageBuilder {

    private static int lineWidth = 1200;

    private ItemStack itemStack;
    private BookMeta meta;

    private int currentWidth;

    private StringBuilder line;
    private boolean ignoreNext;

    public MessageBuilder() {
        this.line = new StringBuilder();
    }

    public void addHeader(String title, char c, char d) {
        fillLine(c);
        addCentralized(title, d);
        fillLine(c);
    }

    public void fillLine(char c) {
        int w = FontWidthCalculator.getCharWidth(c);
        int current = currentWidth;

        do {
            append(c);
            current += w;
        } while (current < lineWidth);
        newLine();
    }

    public void addLine(String string) {
        for (int i = 0; i < string.length(); i++)
            append(string.charAt(i));
    }

    public void addLine(Object... string) {
        int item = lineWidth / string.length;

        for (Object object : string) {
            String str = String.valueOf(object);
            int fill = item - FontWidthCalculator.getStringWidth(str);
            addLine(str);
            append(' ', fill / FontWidthCalculator.getCharWidth(' '));
        }
        newLine();
    }

    public void addCentralized(String string, char with) {
        int cw = FontWidthCalculator.getCharWidth(with);
        int sw = FontWidthCalculator.getStringWidth(string);
        int sides = ((lineWidth - sw) / cw) / 2;

        append(with, sides);
        addLine(string);
        append(with, sides);
        newLine();
    }

    public String getMessage() {
        return line.toString();
    }

    private void append(char c, int quantity) {
        for (int i = 0; i < quantity; i++) append(c);
    }

    private void append(char c) {
        if (currentWidth >= lineWidth) newLine();
        line.append(c);

        if (ignoreNext || c == '§') {
            if (c == '§') ignoreNext = true;
            return;
        }

        ignoreNext = false;

        currentWidth += FontWidthCalculator.getCharWidth(c);
    }

    private void newLine() {
        currentWidth = 0;
        line.append("\n");
    }
}
