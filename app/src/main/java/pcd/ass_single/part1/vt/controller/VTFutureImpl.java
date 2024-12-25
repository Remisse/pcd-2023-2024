package pcd.ass_single.part1.vt.controller;

import pcd.ass_single.part1.common.lock.CloseableReentrantLock;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.Condition;

public class VTFutureImpl<T> implements VTFuture<T> {
    private T res;
    private String error;
    private final CloseableReentrantLock lock = new CloseableReentrantLock();
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
        try (var ignored = lock.lockInterruptiblyAsResource()){
            return isAvailable;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public T get() throws InterruptedException, ExecutionException {
        try (var ignored = lock.lockInterruptiblyAsResource()) {
            while (!isAvailable && !isError) {
                available.await();
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        if (isError) {
            throw new InterruptedException(error);
        }
        return res;
    }

    @Override
    public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        try (var ignored = lock.lockInterruptiblyAsResource()) {
            if (res == null && !available.await(timeout, unit)) {
                throw new TimeoutException();
            }
            return res;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void set(T value) {
        try (var ignored = lock.lockInterruptiblyAsResource()) {
            res = value;
            isAvailable = true;
            available.signalAll();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void setError(String error) {
        try (var ignored = lock.lockInterruptiblyAsResource()) {
            this.error = error;
            isError = true;
            available.signalAll();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean isError() {
        try (var ignored = lock.lockInterruptiblyAsResource()) {
            return isError;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getError() {
        try (var ignored = lock.lockInterruptiblyAsResource()) {
            while (!isError && !isAvailable) {
                available.await();
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        if (isAvailable) {
            throw new IllegalStateException("Result was set while waiting for an error");
        }
        return error;
    }
}
