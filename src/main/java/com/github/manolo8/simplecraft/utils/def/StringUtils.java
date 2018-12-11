package com.github.manolo8.simplecraft.utils.def;

import com.github.manolo8.simplecraft.utils.calculator.FontWidthCalculator;
import net.minecraft.server.v1_13_R2.IChatBaseComponent;
import sun.reflect.ConstructorAccessor;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Date;

public class StringUtils {

    private static final DecimalFormat doubleFormatter;
    private static final DecimalFormat doubleFormatter0D;
    private static final DateFormat dateFormatter;
    private static final ConstructorAccessor stringConstructor;

    static {
        doubleFormatter = new DecimalFormat("###,###,###,###,###.##");
        doubleFormatter0D = new DecimalFormat("###,###,###,###,###");
        dateFormatter = new SimpleDateFormat("d/M/Y");

        ConstructorAccessor accessor = null;

        try {

            Constructor<String> str = String.class.getDeclaredConstructor(char[].class, boolean.class);
            str.setAccessible(true);
            str.newInstance(new char[]{'a', 'b'}, true);
            Field accessorField = str.getClass().getDeclaredField("constructorAccessor");
            accessorField.setAccessible(true);

            accessor = (ConstructorAccessor) accessorField.get(str);

        } catch (Exception e) {
            e.printStackTrace();
        }

        stringConstructor = accessor;
    }

    public static IChatBaseComponent serialize(String value) {
        return IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + value + "\"}");
    }

    public static String toStringWithColors(String s) {
        int first = 0;
        int total = s.length();

        for (; first < total; first++) {
            if (s.charAt(first) == '&') break;
        }

        if (first == total) return s;

        char[] buffer = new char[total];
        s.getChars(0, first, buffer, 0);

        for (; first < total; first++) {
            char c = s.charAt(first);

            if (c == '&' && first + 1 < total) {

                char next = s.charAt(first + 1);

                buffer[first] = ((next >= '0' && next <= '9') || next >= 'a' && next <= 'f') ? '§' : '&';

            } else buffer[first] = c;
        }

        return wrap(buffer);
    }

    public static boolean validateClanTag(String tag) {
        int colors = 0;
        int ch = 0;
        int total = tag.length();

        for (int i = 0; i < total; i++) {
            char c = tag.charAt(i);
            if (c == '§') {

                if (++i >= total) return false;

                c = tag.charAt(i);

                if ((Character.isDigit(c) || c >= 'a' && c <= 'f') && c != '4' && c != '0' && c != 'd') {
                    colors++;
                } else return false;

            } else ch++;
        }


        return ch == 3 && colors <= 3;
    }

    public static String of(String[] args, int index, int length) {
        int size = 0;

        for (int i = index; i < length; i++) {
            size += args[i].length() + (length != i + 1 ? 1 : 0);
        }

        char[] chars = new char[size];

        int current = 0;

        for (int i = index; i < length; i++) {
            String copy = args[i];
            copy.getChars(0, copy.length(), chars, current);
            current += copy.length();
            if (i + 1 != length) chars[current++] = ' ';
        }

        return wrap(chars);
    }

//
//    public static String toString(String[] args, int i, boolean colors) {
//        StringBuilder builder = new StringBuilder();
//
//        while (i < args.length) {
//            builder.append(colors ? toStringWithColors(args[i]) : args[i]);
//            i++;
//            if (i != args.length) builder.append(' ');
//        }
//
//        return builder.toString();
//    }

    public static String removeColors(String s) {
        int occurrences = 0;
        int total = s.length();

        for (int i = 0; i < total; i++)
            if (s.charAt(i) == '§') {
                occurrences += 2;
            }

        if (occurrences == 0) return s;

        int size = total - occurrences;
        char[] buffer = new char[size];
        int pos = 0;

        for (int i = 0; i < total; i++) {
            if (s.charAt(i) == '§') {
                i++;
            } else {
                buffer[pos++] = s.charAt(i);
            }
        }

        return wrap(buffer);
    }

    public static String fill(int size, char fill) {
        char[] chars = new char[size];

        Arrays.fill(chars, fill);

        return wrap(chars);
    }

    public static String longTimeToString(long time) {

        time = time / 1000;

        if (time < 200) {
            return time + " segundo(s)";
        }

        time = time / 60;

        if (time < 200) {
            return time + " minuto(s)";
        }

        time = time / 60;

        if (time < 48) {
            return time + " hora(s)";
        }

        return time / 24 + " dia(s)";
    }

    public static String doubleToString(double number) {
        return doubleFormatter.format(number);
    }

    public static String doubleToString0D(double number) {
        return doubleFormatter0D.format(number);
    }

    public static String longToDate(long date) {
        return dateFormatter.format(new Date(date));
    }

    public static void fill(boolean start, int width, Object original, char c, StringBuilder builder) {
        int quantity = (width - FontWidthCalculator.getStringWidth(String.valueOf(original))) / FontWidthCalculator.getCharWidth(c);

        if (start) {
            for (int i = 0; i < quantity; i++) builder.append(c);
            builder.append(original);
        } else {
            builder.append(original);
            for (int i = 0; i < quantity; i++) builder.append(c);
        }
    }

    public static boolean hasAnyUpperCase(String permission) {
        for (int i = 0; i < permission.length(); i++)
            if (Character.isUpperCase(permission.charAt(i)))
                return true;

        return false;
    }

    public static String wrap(char[] data) {
        try {
            return (String) stringConstructor.newInstance(new Object[]{data, true}) /* '-' */;
        } catch (Exception e) {
            e.printStackTrace();
            return String.valueOf(data);
        }
    }
}
