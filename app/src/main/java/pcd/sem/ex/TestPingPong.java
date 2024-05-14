package pcd.sem.ex;

import java.util.concurrent.Semaphore;

/**
 * Unsynchronized version
 * 
 * @TODO make it sync 
 * @author aricci
 *
 */
public class TestPingPong {
	public static void main(String[] args) {
		final Semaphore pinged = new Semaphore(0);
		final Semaphore ponged = new Semaphore(0);

		new Pinger(pinged, ponged).start();
		new Ponger(pinged, ponged).start();
	}
}
