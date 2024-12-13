package pcd.lab05.monitors.ex_latch;

public class SimpleLatch implements Latch {
    private int nLeft;

    public SimpleLatch(final int count) {
        nLeft = count;
    }

    @Override
    public synchronized void countDown() {
        nLeft = Math.max(0, nLeft - 1);
        if (nLeft == 0) {
            notifyAll();
        }
    }

    @Override
    public synchronized void await() throws InterruptedException {
        if (nLeft > 0) {
            wait();
        }
    }
}
