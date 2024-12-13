package pcd.lab04.ex;

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
		final var pinged = new Semaphore(0);
		final var ponged = new Semaphore(0);
		new Pinger(pinged, ponged).start();
		new Ponger(pinged, ponged).start();
	}

}
