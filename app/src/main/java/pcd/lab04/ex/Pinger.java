package pcd.lab04.ex;

import java.util.concurrent.Semaphore;

public class Pinger extends BaseWorker {
	public Pinger(final Semaphore hasPinged, final Semaphore hasPonged) {
		super(hasPinged, hasPonged);
	}

	public void run() {
		while (true) {
			try {
				System.out.println("ping!");
				hasPinged.release();
				hasPonged.acquire();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}
}