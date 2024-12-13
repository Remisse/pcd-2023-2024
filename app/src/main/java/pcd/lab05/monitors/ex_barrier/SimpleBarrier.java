package pcd.lab05.monitors.ex_barrier;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SimpleBarrier implements Barrier {
    private final int nParticipants;
    private int nWaiting;
    private final Lock lock;
    private final Condition canProceed;

    public SimpleBarrier(int nParticipants) {
        this.nParticipants = nParticipants;
        nWaiting = 0;
        lock = new ReentrantLock();
        canProceed = lock.newCondition();
    }

    @Override
    public void hitAndWaitAll() throws InterruptedException {
        try {
            lock.lock();
            nWaiting++;
            while (nWaiting < nParticipants) {
                canProceed.await();
            }
            canProceed.signal();
        } finally {
            lock.unlock();
        }
    }
}
