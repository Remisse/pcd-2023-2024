package pcd.lab01.ex01.sol;

import java.util.*;

public class ConcurrentSortTwoThreads {

	static final int VECTOR_SIZE = 400_000_000;
	
	public static void main(String[] args) {
	
		log("Generating array...");
		int[] v = genArray(VECTOR_SIZE);
		
		log("Array generated.");
		// dumpArray(v);
		
		log("Spawning workers to do sorting (" + VECTOR_SIZE + " elements)...");
	
		int middle = v.length/2;
		SortingWorker w1 = new SortingWorker("worker-1", v, 0, middle - 1);
		SortingWorker w2 = new SortingWorker("worker-2", v, middle, v.length - 1);
		MergingWorkerTwoParts m = new MergingWorkerTwoParts("merger", v, w1, w2);

		long t0 = System.currentTimeMillis();	
		w1.start();
		w2.start();
		m.start();
		
		try {
			m.join();
			long t1 = System.currentTimeMillis();
			log("Done. Time elapsed: " + (t1 - t0) + " ms");
			// dumpArray(vnew);
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}


	private static int[] genArray(int n) {
		Random gen = new Random(System.currentTimeMillis());
		int v[] = new int[n];
		for (int i = 0; i < v.length; i++) {
			v[i] = gen.nextInt();
		}
		return v;
	}

	
	private static void dumpArray(int[] v) {
		for (int l:  v) {
			System.out.print(l + " ");
		}
		System.out.println();
	}

	private static void log(String msg) {
		System.out.println(msg);
	}
}
