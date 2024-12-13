package pcd.lab04.ex;

import java.util.concurrent.Semaphore;

class BaseWorker extends Thread {
    protected final Semaphore hasPinged;
    protected final Semaphore hasPonged;

    protected BaseWorker(final Semaphore hasPinged, final Semaphore hasPonged) {
        this.hasPinged = hasPinged;
        this.hasPonged = hasPonged;
    }
}
