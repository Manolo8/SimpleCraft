package com.github.manolo8.simplecraft.utils.bot;

import java.util.Base64;

public class IpUtils {

    public static long ipToLong(String ip) {

        long result = 0;
        long current = 0;
        int pos = 3;

        for (int i = 0; i < ip.length(); i++) {
            char c = ip.charAt(i);

            if (c == '.') {

                result |= current << (pos * 8);
                pos--;
                current = 0;

            } else if (c == ':') {
                break;
            } else if (Character.isDigit(c)) {
                current = current * 10 + Character.getNumericValue(c);
            }
        }

        return result | current << (pos * 8);
    }
}
