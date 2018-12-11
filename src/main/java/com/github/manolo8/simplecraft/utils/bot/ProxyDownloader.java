package com.github.manolo8.simplecraft.utils.bot;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.util.HashSet;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ProxyDownloader {

    public static ReaderAlgorithm txtReader = new TxtReader();
    public static ReaderAlgorithm htmlReader = new HtmlReader();
    private HashSet<Long> proxies;

    //https://drive.google.com/uc?export=download&id=1-kvrRya6c0FPCwq_KdIcxWuucRwNTnPA

    public ProxyDownloader() {
        proxies = new HashSet<>();
    }

    public static void main(String[] args) throws IOException {
        ProxyDownloader downloader = new ProxyDownloader();

        System.out.println(downloader.has("101.109.176.229"));
        System.out.println(downloader.has("101.109.246.114"));


        downloader.downloadFromZip("https://drive.google.com/uc?export=download&id=1-kvrRya6c0FPCwq_KdIcxWuucRwNTnPA", txtReader);
        downloader.downloadFromZip("https://drive.google.com/uc?export=download&id=1nTPoGiMo8ZzmZiVZ6bdvMXhNPK7Y_E7l", txtReader);
        downloader.downloadFromZip("https://drive.google.com/uc?export=download&id=1fEdzrzgD88aSKimkEyG0IjEXah9LLIGB", txtReader);
        downloader.downloadFrom("https://getmeproxy.com/api/v1.0/api.php?key=c84d1076312bcf1e875c94d4e20692f5&list=text", htmlReader);

        System.out.println(downloader.proxies.size());
    }

    public boolean has(String ip) {
        return proxies.contains(ipToLong(ip));
    }

    private long ipToLong(String ip) {

        long result = 0;
        long current = 0;
        int pos = 3;

        for (int i = 0; i < ip.length(); i++) {
            char c = ip.charAt(i);

            if (c == '.') {

                result |= current << (pos * 8);
                pos--;
                current = 0;

            } else {
                current = current * 10 + Character.getNumericValue(c);
            }
        }

        return result | current << (pos * 8);
    }


    public void downloadFrom(String url, ReaderAlgorithm algorithm) throws IOException {
        readStream(getInputStream(url), algorithm);
    }

    public void downloadFromZip(String url, ReaderAlgorithm algorithm) throws IOException {
        ZipInputStream stream = new ZipInputStream(getInputStream(url));

        ZipEntry entry;

        while ((entry = stream.getNextEntry()) != null) {
            if (entry.getName().endsWith(".txt")) {
                readStream(stream, algorithm);
            }
        }
    }

    private void readStream(InputStream stream, ReaderAlgorithm algorithm) throws IOException {
        long address = 0;

        while ((address = algorithm.nextAddress(stream)) != -1) {
            proxies.add(address);
        }
    }


    private InputStream getInputStream(String url0) throws IOException {
        URL url = new URL(url0);

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        setupDefaults(connection);

        return connection.getInputStream();
    }

    private void setupDefaults(HttpURLConnection connection) throws ProtocolException {
        connection.setRequestMethod("GET");
        connection.setRequestProperty("User-Agent", "SimpleCraft");
        connection.setConnectTimeout(2000);
        connection.setReadTimeout(2000);
        connection.setDoOutput(true);
    }

    interface ReaderAlgorithm {

        long nextAddress(InputStream stream) throws IOException;
    }

    static class TxtReader implements ReaderAlgorithm {

        @Override
        public long nextAddress(InputStream stream) throws IOException {

            long calc = 0;
            long current = 0;
            int pos = 3;
            int read;

            while (true) {

                read = stream.read();

                if (read == -1) return -1;

                char c = (char) read;

                if (c == '.' || c == ':') {

                    calc |= current << (pos * 8);
                    pos--;
                    current = 0;

                    if (c == ':') {
                        break;
                    }

                } else {
                    current = current * 10 + Character.getNumericValue(c);
                }
            }

            return calc;
        }
    }

    static class HtmlReader implements ReaderAlgorithm {

        @Override
        public long nextAddress(InputStream stream) throws IOException {

            long calc = 0;
            long current = 0;
            int pos = 3;
            int read;


            while (true) {

                read = stream.read();

                if (read == -1) return -1;

                char c = (char) read;

                if (c == '.' || c == ':') {

                    calc |= current << (pos * 8);
                    pos--;
                    current = 0;

                    if (c == ':') {
                        break;
                    }

                } else {
                    current = current * 10 + Character.getNumericValue(c);
                }
            }

            while (true) {

                read = stream.read();

                if (read == -1) return calc;

                char c = (char) read;

                if (c == '>') {
                    break;
                }

            }

            return calc;
        }
    }
}
