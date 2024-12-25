package pcd.ass_single.part1.vt.controller;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class VTFutureImpl<T> implements VTFuture<T> {
    private T res;
    private String error;
    private final ReentrantLock lock = new ReentrantLock();
    private final Condition available = lock.newCondition();
    private boolean isAvailable;
    private boolean isError;

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return false;
    }

    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public boolean isDone() {
        try {
            lock.lockInterruptibly();
            return isAvailable;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public T get() throws InterruptedException, ExecutionException {
        try {
            lock.lockInterruptibly();
            while (!isAvailable && !isError) {
                available.await();
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
        if (isError) {
            throw new InterruptedException(error);
        }
        return res;
    }

    @Override
    public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        try {
            lock.lockInterruptibly();
            if (res == null && !available.await(timeout, unit)) {
                throw new TimeoutException();
            }
            return res;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void set(T value) {
        try {
            lock.lockInterruptibly();
            res = value;
            isAvailable = true;
            available.signalAll();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void setError(String error) {
        try {
            lock.lockInterruptibly();
            this.error = error;
            isError = true;
            available.signalAll();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean isError() {
        try {
            lock.lockInterruptibly();
            return isError;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public String getError() {
        try {
            lock.lockInterruptibly();
            while (!isError && !isAvailable) {
                available.await();
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
        if (isAvailable) {
            throw new IllegalStateException("Result was set while waiting for an error");
        }
        return error;
    }
}
