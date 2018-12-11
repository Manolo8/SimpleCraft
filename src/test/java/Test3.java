import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.util.ArrayList;

public class Test3 {

    final static SoftReference<Object> reference2 = new SoftReference<Object>(new Object());

    public static void main(String[] args) {

        new Test().start();

        ReferenceQueue referenceQueue = new ReferenceQueue();

        for (int i = 0; i < 1000; i++) {
            final SoftReference<Object> reference = new MappedSoftReference<>(new Object(), referenceQueue);

            // Sanity check

            // Force an OoM
            try {
                final ArrayList<Object[]> allocations = new ArrayList<Object[]>();
                int size;
                while ((size = Math.min(Math.abs((int) Runtime.getRuntime().freeMemory()), Integer.MAX_VALUE)) > 0)
                    allocations.add(new Object[size]);
            } catch (OutOfMemoryError e) {
                // great!
            }

            // Verify object has been garbage collected
        }
    }

    static class Test extends Thread {

        @Override
        public void run() {

            ReferenceQueue referenceQueue = new ReferenceQueue() {
                @Override
                public Reference remove() throws InterruptedException {
                    Reference reference = super.remove();

                    System.out.println((reference == null) + " ...");

                    return reference;
                }
            };

            final SoftReference<Object> reference = new MappedSoftReference<>(new Object(), referenceQueue);

            while (true) {

                Reference object;
                if ((object = referenceQueue.poll()) != null) {
                    System.out.println("... null " + object.get() == null);
                }

                System.out.println();

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    static class MappedSoftReference<T> extends SoftReference<T> {

        public MappedSoftReference(T referent, ReferenceQueue<? super T> q) {
            super(referent, q);
        }

        public MappedSoftReference(T referent) {
            super(referent);
        }
    }
}
