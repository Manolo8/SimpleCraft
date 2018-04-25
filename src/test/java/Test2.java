import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Test2 {

    public static void main(String[] args) {
        for (int i = 0; i < 210; i += 20) {
            System.out.println(i % 30);
        }
    }

//    public static void main(String[] args) throws IOException {
//        File file = new File("D:\\test.txt");
//
//        FileWriter writer = new FileWriter(file);
//
//        int i = 0;
//        long length = 0;
//
//        while (i < Integer.MAX_VALUE) {
//            i++;
//
//            String value = String.valueOf(i);
//            int addLength = value.length() + 3;
//
//            if (length + addLength > 150) {
//                writer.write('\n');
//                length = 0;
//            } else length += addLength;
//
//            writer.write(value);
//            writer.write(" - ");
//        }
//
//        writer.close();
//    }
}
