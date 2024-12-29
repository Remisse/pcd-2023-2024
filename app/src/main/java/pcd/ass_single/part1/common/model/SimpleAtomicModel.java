package pcd.ass_single.part1.common.model;

import pcd.ass_single.part1.common.lock.CloseableReentrantLock;

import java.util.function.Consumer;

public class SimpleAtomicModel implements Model, ConsumableModel<ModelState> {
    private int total;
    private int parsed;
    private int found;
    private final CloseableReentrantLock totalLock = new CloseableReentrantLock();
    private final CloseableReentrantLock parsedLock = new CloseableReentrantLock();
    private final CloseableReentrantLock foundLock = new CloseableReentrantLock();

    @Override
    public void incrementTotal(int n) {
        try (var ignored = totalLock.lockAsResource()) {
            total += ensureNumberIsPositive(n);
        }
    }

    @Override
    public void incrementParsed(int n) {
        try (var ignored = parsedLock.lockAsResource()) {
            parsed += ensureNumberIsPositive(n);
        }
    }

    @Override
    public void incrementFound(int n) {
        try (var ignored = foundLock.lockAsResource()) {
            found += ensureNumberIsPositive(n);
        }
    }

    @Override
    public void consumeState(Consumer<ModelState> consumer) {
        acquireAllAndThen(() -> consumer.accept(new ModelState(total, parsed, found)));
    }

    @Override
    public void reset() {
        acquireAllAndThen(() -> {
            total = 0;
            parsed = 0;
            found = 0;
        });
    }

    private void acquireAllAndThen(Runnable runnable) {
        try (var ignored = totalLock.lockAsResource()) {
            try (var ignored2 = parsedLock.lockAsResource()) {
                try (var ignored3 = foundLock.lockAsResource()) {
                    runnable.run();
                }
            }
        }
    }

    private static int ensureNumberIsPositive(int n) {
        if (n <= 0) {
            throw new IllegalArgumentException("Supplied a non-positive number (" + n + ")");
        }
        return n;
    }
}
