package pcd.ass_single.part1.common.lock;

import java.util.concurrent.locks.ReentrantLock;

public class CloseableReentrantLock extends ReentrantLock {
    public ResourceLock lockAsResource() {
        lock();
        return this::unlock;
    }
}
