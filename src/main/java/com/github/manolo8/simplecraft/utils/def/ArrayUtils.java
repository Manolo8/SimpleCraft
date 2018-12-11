package com.github.manolo8.simplecraft.utils.def;

public class ArrayUtils {

    public static String[] addFirst(String[] original, String value) {
        String[] temp = new String[original.length + 1];

        temp[0] = value;

        System.arraycopy(original, 0, temp, 1, original.length);

        return temp;
    }
}
