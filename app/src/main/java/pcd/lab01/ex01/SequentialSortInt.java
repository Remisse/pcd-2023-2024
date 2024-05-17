package pcd.lab01.ex01;

import java.util.*;

public class SequentialSortInt {

    static final int VECTOR_SIZE = 400_000_000;

    public static void main(String[] args) {

        log("Generating array...");
        int[] v = genArray(VECTOR_SIZE);

        log("Array generated.");
        log("Sorting (" + VECTOR_SIZE + " elements)...");

        long t0 = System.nanoTime();
        Arrays.sort(v);
        //v = sortArray(v);
        long t1 = System.nanoTime();
        log("Done. Time elapsed: " + ((t1 - t0) / 1000000) + " ms");

        //dumpArray(v);
    }

    private static int[] genArray(int n) {
        Random gen = new Random(System.currentTimeMillis());
        int[] v = new int[n];
        for (int i = 0; i < v.length; i++) {
            v[i] = gen.nextInt();
        }
        return v;
    }

    private static int[] sortArray(int[] v) {
        final int threadCount = Runtime.getRuntime().availableProcessors();
        final var threads = new Thread[threadCount];
        final int slice = v.length / threadCount;
        for (int i = 0; i < threads.length; ++i) {
            final int startIdx = slice * i;
            final int endIdx = Math.min(slice * (i + 1), v.length) - 1;
            threads[i] = new Thread(() -> Arrays.sort(v, startIdx, endIdx));
            threads[i].start();
        }
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        return merge(v, threadCount);
    }

    private static int[] merge(int[] v, int nParts) {
        int[] vnew = new int[v.length];

        int partSize = v.length/nParts;
        int from = 0;

        int[] indexes = new int[nParts];
        int[] max = new int[nParts];
        for (int i = 0; i < indexes.length - 1; i++) {
            indexes[i] = from;
            max[i] = from + partSize;
            from = max[i];
        }
        indexes[indexes.length - 1] = from;
        max[indexes.length - 1] = v.length;

        int i3 = 0;
        boolean allFinished = false;
        while (!allFinished) {

            int min = Integer.MAX_VALUE;
            int index = -1;
            for (int i = 0; i < indexes.length; i++) {
                if (indexes[i] < max[i]) {
                    if (v[indexes[i]] < min) {
                        index = i;
                        min = v[indexes[i]];
                    }
                }
            }

            if (index != -1) {
                vnew[i3] = v[indexes[index]];
                indexes[index]++;
                i3++;
            } else {
                allFinished = true;
            }
        }
        return vnew;
    }

    private static void dumpArray(int[] v) {
        for (int l : v) {
            System.out.print(l + " ");
        }
        System.out.println();
    }

    private static void log(String msg) {
        System.out.println(msg);
    }
}
