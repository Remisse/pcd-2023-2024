package pcd.ass_single.part1.common.flag;

import pcd.ass_single.part1.common.lock.CloseableReentrantLock;

public class AtomicFlag {
    private final CloseableReentrantLock lock = new CloseableReentrantLock();
    private boolean value;

    public boolean isSet() {
        try (var ignored = lock.lockAsResource()) {
            return value;
        }
    }

    public void set() {
        try (var ignored = lock.lockAsResource()) {
            value = true;
        }
    }

    public void reset() {
        try (var ignored = lock.lockAsResource()) {
            value = false;
        }
    }
}
