package pcd.sem.ex;

import java.util.concurrent.Semaphore;

public class Pinger extends Thread {

	private final Semaphore pinged;
	private final Semaphore ponged;

	public Pinger(Semaphore pinged, Semaphore ponged) {
		this.pinged = pinged;
		this.ponged = ponged;
	}	
	
	public void run() {
		while (true) {
			try {
				ponged.acquire();
				System.out.println("ping!");
				pinged.release();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}
}