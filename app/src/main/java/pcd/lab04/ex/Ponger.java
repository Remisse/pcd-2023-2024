package pcd.lab04.ex;

import java.util.concurrent.Semaphore;

public class Ponger extends BaseWorker {
	public Ponger(final Semaphore hasPinged, final Semaphore hasPonged) {
		super(hasPinged, hasPonged);
	}

	public void run() {
		while (true) {
			try {
				hasPinged.acquire();
				System.out.println("pong!");
				hasPonged.release();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}
}