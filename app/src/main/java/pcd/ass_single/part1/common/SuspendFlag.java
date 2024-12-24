package pcd.ass_single.part1.common;

import pcd.ass_single.part1.common.lock.CloseableReentrantLock;

import java.util.concurrent.locks.Condition;

public class SuspendFlag extends CloseableReentrantLock {
    private boolean shouldSuspend = false;
    private final Condition condition = newCondition();

    public void tryAwait() {
        try (var ignored = lockAsResource()){
            while (shouldSuspend) {
                condition.await();
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void suspend() {
        try (var ignored = lockAsResource()) {
            shouldSuspend = true;
        }
    }

    public void resume() {
        try (var ignored = lockAsResource()) {
            shouldSuspend = false;
            condition.signalAll();
        }
    }
}
