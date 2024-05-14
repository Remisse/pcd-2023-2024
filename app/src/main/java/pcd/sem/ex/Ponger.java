package pcd.sem.ex;

import java.util.concurrent.Semaphore;

public class Ponger extends Thread {

	private final Semaphore pinged;
	private final Semaphore ponged;
	
	public Ponger(Semaphore pinged, Semaphore ponged) {
		this.pinged = pinged;
		this.ponged = ponged;
	}	
	
	public void run() {
		while (true) {
			try {
				pinged.acquire();
				System.out.println("pong!");
				ponged.release();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}
}