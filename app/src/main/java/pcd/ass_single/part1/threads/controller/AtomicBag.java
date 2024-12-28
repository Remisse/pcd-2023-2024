package pcd.ass_single.part1.threads.controller;

import pcd.ass_single.part1.common.Logger;
import pcd.ass_single.part1.common.lock.CloseableReentrantLock;

import java.util.*;
import java.util.concurrent.locks.Condition;

public class AtomicBag<T> implements Bag<T> {
    private static final Logger LOGGER = Logger.get();
    private final List<T> queue = new ArrayList<>();
    private final CloseableReentrantLock lock = new CloseableReentrantLock();
    private final Condition proceed = lock.newCondition();
    private boolean isBeingClosed;
    private final int threadCount;
    private int currentThreads;

    public AtomicBag(final int threadCount) {
        this.threadCount = threadCount;
    }

    @Override
    public void put(T item) {
        Objects.requireNonNull(item);
        try (var ignored = lock.lockInterruptiblyAsResource()) {
            queue.add(item);
            proceed.signalAll();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void putAll(List<T> items) {
        if (items.isEmpty()) {
            return;
        }
        try (var ignored = lock.lockInterruptiblyAsResource()) {
            queue.addAll(items);
            proceed.signalAll();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<T> checkInAndAwait() {
        try (var ignored = lock.lockInterruptiblyAsResource()) {
            currentThreads++;
            while (!isBeingClosed && willNewItemsBeAdded()) {
                LOGGER.debugLog(Thread.currentThread().getName(), "waiting for a new item");
                proceed.await();
            }
            if (isBeingClosed || shouldQuit()) {
                LOGGER.debugLog(Thread.currentThread().getName(), "leaving bag");
                proceed.signal();
                return Optional.empty();
            }
            currentThreads--;
            if (queue.isEmpty()) {
                throw new IllegalStateException("Queue is empty");
            }
            return Optional.of(queue.removeLast());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean willNewItemsBeAdded() {
        return queue.isEmpty() && currentThreads < threadCount;
    }

    private boolean shouldQuit() {
        return queue.isEmpty() && currentThreads == threadCount;
    }

    @Override
    public void close() {
        try (var ignored = lock.lockInterruptiblyAsResource()) {
            isBeingClosed = true;
            proceed.signalAll();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
