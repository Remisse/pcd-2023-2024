package pcd.ass_single.part1.threads.controller;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class SuspendFlag extends ReentrantLock {
    private boolean shouldSuspend = false;
    private final Condition condition = newCondition();

    public void tryAwait() {
        try {
            lock();
            while (shouldSuspend) {
                condition.await();
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            unlock();
        }
    }

    public void suspend() {
        try {
            lock();
            shouldSuspend = true;
        } finally {
            unlock();
        }
    }

    public void resume() {
        try {
            lock();
            shouldSuspend = false;
            condition.signalAll();
        } finally {
            unlock();
        }
    }
}
