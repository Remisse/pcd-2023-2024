package pcd.ass_single.part1.common.flag;

import pcd.ass_single.part1.common.lock.CloseableReentrantLock;

import java.util.concurrent.locks.Condition;

public class SuspendableFlag {
    private final CloseableReentrantLock lock = new CloseableReentrantLock();
    private final Condition awaitCondition = lock.newCondition();
    private boolean shouldAwait = false;

    public void checkIn() {
        try (var ignored = lock.lockAsResource()){
            while (shouldAwait) {
                awaitCondition.await();
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void set() {
        try (var ignored = lock.lockAsResource()) {
            shouldAwait = true;
        }
    }

    public void reset() {
        try (var ignored = lock.lockAsResource()) {
            shouldAwait = false;
            awaitCondition.signalAll();
        }
    }
}
