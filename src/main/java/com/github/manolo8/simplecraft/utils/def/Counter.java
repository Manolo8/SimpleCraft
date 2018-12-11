package com.github.manolo8.simplecraft.utils.def;

import java.util.List;

public class Counter {

    public static <E> int min(List<E> list, ToStringFunction<E> toString, char search) {
        int min = 1;

        main:
        for (E e : list) {
            String s = toString.from(e);
            int i = 0;
            for (; i < s.length(); i++) {
                if (s.charAt(i) != search) {
                    continue main;
                }
            }

            if (i >= min) min = i + 1;
        }

        System.out.println(min);

        return min;
    }

    public interface ToStringFunction<E> {
        String from(E e);
    }
}
