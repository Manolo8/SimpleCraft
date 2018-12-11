package com.github.manolo8.simplecraft.utils.bot;

import com.github.manolo8.simplecraft.module.user.UserService;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashSet;

import static com.github.manolo8.simplecraft.utils.bot.IpUtils.ipToLong;
import static sun.net.www.protocol.http.HttpURLConnection.userAgent;

public class BotDetector {

    private HashSet<Long> lastPings;
    private long[] last10Logins = new long[10];
    private int current;
    private JsonParser parser = new JsonParser();

    public BotDetector() {
        this.lastPings = new HashSet<>();
    }

    public boolean cantConnect(final String address) {
        return UserService.countAddress(ipToLong(address)) == 3;
    }

    public boolean disallow(final String name, final String address) {

        final int lastLogins = lastLogins();
        final boolean nameBot = checkName(name);
        final boolean ip = checkProxy(address);
        final boolean country = checkCountry(address);

        return (((nameBot || ip || country) && lastLogins > 3));
    }

    private int lastLogins() {
        int calc = 0;

        for (long time : last10Logins) {
            if (System.currentTimeMillis() - time < 1000 * 15) {
                calc++;
            }
        }

        last10Logins[current++ % 10] = System.currentTimeMillis();

        return calc;
    }

    public void onIpPing(final String address) {
        lastPings.add(ipToLong(address));
    }

    public boolean isAddedToServerList(final String address) {
        return lastPings.contains(ipToLong(address));
    }

    private boolean checkCountry(final String ip) {
        try {
            URL website = new URL("https://ipinfo.io/" + ip + "/json");
            URLConnection connection = website.openConnection();
            connection.setConnectTimeout(1000);
            connection.setRequestProperty("User-Agent", userAgent);
            connection.setDoOutput(true);
            connection.setDoInput(true);

            JsonObject result = (JsonObject) parser.parse(new InputStreamReader(connection.getInputStream()));

            String country = result.get("country").getAsString();

            return !(country.equals("PT") || country.equals("BR"));

        } catch (Exception e) {
            return false;
        }
    }

    private boolean checkProxy(String ip) {
        return checkProxyByGetIpIntel(ip) || checkProxyByProxyCheck(ip);
    }

    private boolean checkProxyByProxyCheck(String ip) {
        try {
            URL website = new URL("http://proxycheck.io/v2/" + ip);
            URLConnection connection = website.openConnection();
            connection.setConnectTimeout(1000);
            connection.setRequestProperty("User-Agent", userAgent);
            connection.setDoOutput(true);
            connection.setDoInput(true);

            JsonObject result = (JsonObject) parser.parse(new InputStreamReader(connection.getInputStream()));

            return ((JsonObject) result.get(ip)).get("proxy").getAsString().equals("yes");

        } catch (Exception e) {
            return false;
        }
    }

    private boolean checkProxyByGetIpIntel(String ip) {
        try {
            URL website = new URL("http://check.getipintel.net/check.php?flags=m&contact=cron1001@gmail.com&ip=" + ip);
            URLConnection connection = website.openConnection();
            connection.setConnectTimeout(1000);
            connection.setRequestProperty("User-Agent", userAgent);
            connection.setDoOutput(true);
            connection.setDoInput(true);

            try (Reader reader = new InputStreamReader(connection.getInputStream())) {
                int read = reader.read();

                return read != -1 && (char) read == '1';
            }

        } catch (Exception e) {
            return false;
        }
    }

    private boolean checkName(String name) {
        int total = name.length();

        int high = 0;
        int numbers = 0;
        int e = 0;
        int s = 0;
        int rep = 0;

        for (int i = 0; i < total; i++) {
            char c = name.charAt(i);

            for (int r = i; r < total; r++) {
                if (i != r && c == name.charAt(r)) rep++;
            }

            if (Character.isLetter(c)) {
                if (Character.isUpperCase(c)) {
                    high++;
                    if (c == 'A' || c == 'E' || c == 'I' || c == 'O' || c == 'U' || c == 'Y') {
                        e++;
                        if (i > 0 && Character.isLetter(name.charAt(i - 1))) s++;
                    }
                } else {
                    if (c == 'a' || c == 'e' || c == 'i' || c == 'o' || c == 'u' || c == 'y' || c == '0' || c == '4') {
                        e++;
                        if (i > 0 && Character.isLetter(name.charAt(i - 1))) s++;
                    }
                }
            } else if (c >= '0' && c <= '9') {
                numbers++;
            }
        }


        //Testa simples (Os caras não vão saber sobre isso, então, sorte minha =) )
        return (s + (high < 2 ? 1 : -1) + (numbers < 0 ? 1 : -2) + ((numbers + high) <= e ? 1 : -1) + (total - rep < 10 ? 2 : -1)) < 0;
    }
}
