package pcd.lab01.ex01.sol;

import java.util.*;

public class ConcurrentSortGeneral {

	static final int VECTOR_SIZE = 400_000_000;
	static final boolean isDebugging = false;
	
	public static void main(String[] args) {
	
		log("Generating array...");
		int[] v = genArray(VECTOR_SIZE);
		
		log("Array generated.");
		if (isDebugging) {
			dumpArray(v);
		}
		
		long t0 = System.currentTimeMillis();	
		log("Spawning workers to do sorting (" + VECTOR_SIZE + " elements)...");
	
		
		int nAgents = Runtime.getRuntime().availableProcessors();
		int jobSize = v.length/nAgents;
		int from = 0; 
		int to = jobSize - 1;
		
		List<SortingWorker> workers = new ArrayList<SortingWorker>();
		for (int i = 0; i < nAgents - 1; i++) {
			var w = new SortingWorker("worker-"+(i+1), v, from, to);
			w.start();
			workers.add(w);
			from = to + 1;
			to += jobSize;
		}
		var w = new SortingWorker("worker-"+nAgents, v, from, v.length - 1);
		w.start();
		workers.add(w);

		MergingWorkerGeneral m = new MergingWorkerGeneral("merger", v, workers);
		m.start();
		try {
			m.join();
			long t1 = System.currentTimeMillis();
			log("Done. Time elapsed: " + (t1 - t0) + " ms");
			
			if (isDebugging) {
				dumpArray(v);
				assert (isSorted(v));
			}
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}


	private static int[] genArray(int n) {
		int numElem = n;
		if (isDebugging) {
			numElem = 100;
			Random gen = new Random(System.currentTimeMillis());
			int v[] = new int[numElem];
			for (int i = 0; i < v.length; i++) {
				v[i] = gen.nextInt() % 100;
			}
			return v;
		} else {
			Random gen = new Random(System.currentTimeMillis());
			int v[] = new int[numElem];
			for (int i = 0; i < v.length; i++) {
				v[i] = gen.nextInt();
			}
			return v;
		}
	}

	private static boolean isSorted(int[] v) {
		if (v.length == 0) {
			return true;
		} else {
			int curr = v[0];
			for (int i = 1; i < v.length; i++) {
				if (curr > v[i]) {
					return false;
				} else {
					curr = v[i];
				}
			}
			return true;
		}
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
