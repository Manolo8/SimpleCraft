import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class OnlineThread extends Thread {

    private final String[] data;
    public int current = 25550;
    public int index;
    public boolean locked;
    private InetSocketAddress address;

    public OnlineThread(InetSocketAddress address, int index) {
        this.address = new InetSocketAddress(address.getAddress(), current);
        this.data = new String[3];
        this.data[0] = this.data[1] = this.data[2] = "NOT";
        this.index = index;
    }

    public String getAddress() {
        return address.getHostString() + ":" + (current - 1);
    }

    public String[] getData() {
        return this.data;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Socket socket = new Socket();

                long ping = System.currentTimeMillis();
                socket.connect(this.address, 500);
                DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
                DataInputStream dis = new DataInputStream(socket.getInputStream());
                dos.write(0xFE);
                ping = System.currentTimeMillis() - ping;

                BufferedReader br = new BufferedReader(new InputStreamReader(dis, StandardCharsets.ISO_8859_1));

                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                    sb.append('\n');
                }

                String[] splited = sb.toString().split("\\xA7");

                data[0] = splited[splited.length - 1];
                data[1] = splited[splited.length - 2];
                data[2] = String.valueOf(ping);

                System.out.println(Arrays.toString(splited));

                locked = true;

                sleep(250);
                socket.close();
            } catch (Exception e) {
                if (!(e instanceof SocketTimeoutException)) e.printStackTrace();
                if (locked) continue;
                address = new InetSocketAddress(address.getAddress(), current++);
            }
        }
    }
}
