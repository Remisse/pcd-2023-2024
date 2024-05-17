package pcd.lab05.monitors.ex_latch;

public interface Latch {

	void countDown();

	void await() throws InterruptedException;
}
