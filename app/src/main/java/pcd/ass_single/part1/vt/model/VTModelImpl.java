package pcd.ass_single.part1.vt.model;

import pcd.ass_single.part1.common.lock.CloseableReentrantLock;

import java.util.function.Consumer;

public class VTModelImpl implements VTModel {
    private int total;
    private int parsed;
    private int found;
    private final CloseableReentrantLock lock = new CloseableReentrantLock();

    @Override
    public void incrementTotal(int n) {
        try (var ignored = lock.lockAsResource()) {
            total += ensureNumberIsPositive(n);
        }
    }

    @Override
    public void incrementParsed(int n) {
        try (var ignored = lock.lockAsResource()) {
            parsed += ensureNumberIsPositive(n);
        }
    }

    @Override
    public void incrementFound(int n) {
        try (var ignored = lock.lockAsResource()) {
            found += ensureNumberIsPositive(n);
        }
    }

    @Override
    public void consumeState(Consumer<ModelState> consumer) {
        try (var ignored = lock.lockAsResource()) {
            consumer.accept(new ModelState(total, parsed, found));
        }
    }

    @Override
    public void reset() {
        try (var ignored = lock.lockAsResource()) {
            total = 0;
            parsed = 0;
            found = 0;
        }
    }

    private static int ensureNumberIsPositive(int n) {
        if (n <= 0) {
            throw new IllegalArgumentException("Supplied a non-positive number (" + n + ")");
        }
        return n;
    }
}
