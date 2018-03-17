public class Test2 {

    public static void main(String[] args) {

    }

    public static int solution2(int x, int y) {
        int index;
        if (x * x >= y * y) {
            index = 4 * x * x - x - y;
            if (x < y)
                index = index - 2 * (x - y);
        } else {
            index = 4 * y * y - x - y;
            if (x < y)
                index = index + 2 * (x - y);
        }

        return index;
    }
}
