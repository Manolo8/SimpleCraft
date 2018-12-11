import com.github.manolo8.simplecraft.utils.bot.BotDetector;
import com.github.manolo8.simplecraft.utils.bot.ProxyDownloader;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import static com.github.manolo8.simplecraft.utils.bot.ProxyDownloader.htmlReader;
import static com.github.manolo8.simplecraft.utils.bot.ProxyDownloader.txtReader;

public class Test4 {

    public static void main(String[] args) throws IOException {

        File file = new File("C:\\Users\\willi\\OneDrive\\Área de Trabalho\\test\\test");

        Iterator<Path> i = Files.list(file.toPath()).iterator();

        final Queue<String> ips = new ConcurrentLinkedQueue();
        final HashSet<String> names = new HashSet<>();

        StringBuilder builder = new StringBuilder();

        while (i.hasNext()) {
            Reader reader = new FileReader(i.next().toFile());

            int read;
            boolean is = false;

            while ((read = reader.read()) != -1) {
                char c = (char) read;

                if (c == '[') {
                    int next = reader.read();
                    if (next != -1 && (char) next == '/') {
                        is = true;
                        String name = builder.toString();
                        ips.add(name);
                        builder.setLength(0);
                    }
                } else if (is && c == ':') {
//                    ips.add(builder.toString());
                    is = false;
                } else if (c == ' ') {
                    builder.setLength(0);
                } else if (is) {
                    builder.append(c);
                } else {
                    builder.append(c);
                }
            }
        }

        ProxyDownloader downloader = new ProxyDownloader();

        downloader.downloadFromZip("https://drive.google.com/uc?export=download&id=1-kvrRya6c0FPCwq_KdIcxWuucRwNTnPA", txtReader);
        downloader.downloadFromZip("https://drive.google.com/uc?export=download&id=1nTPoGiMo8ZzmZiVZ6bdvMXhNPK7Y_E7l", txtReader);
        downloader.downloadFromZip("https://drive.google.com/uc?export=download&id=1-kvrRya6c0FPCwq_KdIcxWuucRwNTnPA", txtReader);
        downloader.downloadFrom("https://getmeproxy.com/api/v1.0/api.php?key=c84d1076312bcf1e875c94d4e20692f5&list=text", htmlReader);

        final BotDetector detector = new BotDetector();

        for (int t = 0; t < 30; t++) {
            new Thread(() -> {
                String next;

                while (true) {
                    synchronized (ips) {
                        if ((next = ips.poll()) == null) break;
                    }

                }
            }).start();
        }
    }
}
