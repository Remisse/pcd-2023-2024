package pcd.lab04.ex.sol;

import java.util.concurrent.Semaphore;

public class Ponger extends Thread {
	
	private Semaphore pongDone;
	private Semaphore pingDone;

	public Ponger(Semaphore pingDone, Semaphore pongDone) {
		this.pingDone = pingDone;
		this.pongDone = pongDone;
	}	
	
	public void run() {
		while (true) {
			try {
				pingDone.acquire();
				System.out.println("pong!");
				pongDone.release();				
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}
}