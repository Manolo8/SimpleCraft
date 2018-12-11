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
        String str = "201.71.42.132,186.210.199.225,149.202.70.174,158.69.22.85,149.56.29.162,159.203.12.59,144.217.206.117,128.199.217.159,62.210.45.5,167.249.130.85,94.199.212.249,64.125.109.172,158.69.127.149,24.93.150.129,99.253.24.121,216.27.81.9,45.35.109.178,149.56.107.144,107.152.104.181,144.217.186.144,87.132.50.36,200.0.12.83,134.255.254.215,79.103.16.117,138.207.245.8,84.200.149.30,189.41.83.94,201.162.102.67,213.46.5.152,185.44.78.63,2001:5b0:4fc1:9a40:0:ff:febf:76e9,94.199.212.249,149.56.29.140,176.31.105.77,73.228.2.53,189.41.89.157,85.245.70.136,173.90.168.225,158.69.149.216,186.207.31.86,107.77.70.48,192.99.36.201,177.124.188.150,185.243.155.5,144.217.49.253,191.183.6.63,149.56.243.123,186.210.101.137,54.37.63.148,46.12.232.232";

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
        frame.pack();

        while (true) {
            try {
                sleep(10);


                int i = 0;
                for (OnlineThread thread : threadList) {
                    if (thread.getData()[1].equals("NOT") || thread.getData()[1].contains("0")) continue;
                    table.setValueAt(thread.getAddress(), i, 0);
                    table.setValueAt(thread.getData()[0], i, 1);
                    table.setValueAt(thread.getData()[1], i, 2);
                    table.setValueAt(thread.getData()[2], i, 3);
                    i++;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
