import java.nio.ByteBuffer;

public class Test5 {

    public static void main(String[] args) {

        String name = "saASDÉASDlkfasáeAS<LDÇ´.asd<";
        byte[] nameByte = name.getBytes();

        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES + Short.BYTES + nameByte.length);

        buffer.putLong(4156465);
        buffer.putShort((short) nameByte.length);
        buffer.put(nameByte);
        buffer.rewind();

        long c = buffer.getLong();
        short d = buffer.getShort();
        byte[] data = new byte[d];
        buffer.get(data);


        System.out.println(c);
        System.out.println(d);
        System.out.println(new String(data));
    }

}
