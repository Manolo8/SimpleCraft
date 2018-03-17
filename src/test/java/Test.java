import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class Test extends Thread {

    public static List<OnlineThread> threadList;
    public static int i = 0;

    public Test(String[] address) throws UnknownHostException {
        threadList = new ArrayList<>();

        for (String string : address) {
            InetSocketAddress socketAddress = new InetSocketAddress(InetAddress.getByName(string), 25565);
            OnlineThread thread = new OnlineThread(socketAddress, i);
            thread.locked = false;
            thread.start();
            threadList.add(thread);
            i++;
        }
    }

    public static void createThread(InetSocketAddress address, int port) {
        OnlineThread thread = new OnlineThread(address, i);
        thread.locked = false;
        thread.current = port;
        thread.start();
        threadList.add(thread);
        i++;
    }

    public static void main(String[] args) throws IOException {
        String str = "108.178.18.94,179.96.191.1,187.4.24.211,158.69.249.155,149.56.29.162,191.178.201.50,188.167.250.172,177.83.7.227,158.69.122.26,178.33.137.128,62.210.45.5,103.89.85.4,89.203.248.80,173.230.132.170,177.93.67.239,158.69.117.125,185.212.200.194,192.99.147.89,84.200.149.30,187.51.180.2";
        String[] address = str.split(",");
        Test test = new Test(address);
        test.start();
    }

    public void run() {
        JTable table = new JTable(new Object[threadList.size()][4], new String[]{"ADDRESS", "MAXIMO", "ONLINE", "PING"});
        JFrame frame = new JFrame();
        table.setAutoCreateRowSorter(true);

        frame.setVisible(true);
        frame.setSize(new Dimension(640, 360));

        JScrollPane sp = new JScrollPane(table);
        table.setFillsViewportHeight(true);
        sp.setVisible(true);
        frame.add(sp);

        frame.setLayout(new BorderLayout());
        frame.add(table.getTableHeader(), BorderLayout.PAGE_START);
        frame.add(table, BorderLayout.CENTER);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        while (true) {
            try {
                sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            int i = 0;
            for (OnlineThread thread : threadList) {
                if (thread.getData()[1].equals("NOT")) continue;
                i++;
                table.setValueAt(thread.getAddress(), i, 0);
                table.setValueAt(thread.getData()[0], i, 1);
                table.setValueAt(thread.getData()[1], i, 2);
                table.setValueAt(thread.getData()[2], i, 3);
            }
        }
    }
}
