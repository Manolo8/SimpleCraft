import sun.reflect.ConstructorAccessor;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

public class Test7 {

    public static void main(String[] args) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException, NoSuchFieldException {

        Constructor<String> str = String.class.getDeclaredConstructor(char[].class, boolean.class);
        str.setAccessible(true);


        char[] test = "482903820948901280948-2193-029309124i-9j19d92j09h3t09f3j093jq2d0ijt8h310u409jdoksjf8038u4-912ioijlwkeef3h3u49-i129-uiowjkljfer".toCharArray();

        str.newInstance(test, true);

        Field accessorField = str.getClass().getDeclaredField("constructorAccessor");
        accessorField.setAccessible(true);

        ConstructorAccessor accessor = (ConstructorAccessor) accessorField.get(str);

        long time1 = 0;
        long time2 = 0;

        for (int i = 0; i < 50000000; i++) {

            if (i % 2 == 0) {
                long time = System.nanoTime();
                String p = (String) accessor.newInstance(new Object[]{test, true});
                time1 += System.nanoTime() - time;
            } else {
                long time = System.nanoTime();
                String p = String.valueOf(test);
                time2 += System.nanoTime() - time;
            }

        }

        System.out.println(time1);
        System.out.println(time2);
    }

    public static Object[] test(Object... args) {
        return args;
    }
}
