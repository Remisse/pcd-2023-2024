package pcd.ass_single.part1.task.controller.tasks;

import pcd.ass_single.part1.common.flag.SuspendableFlag;
import pcd.ass_single.part1.common.Logger;

import java.util.concurrent.RecursiveTask;

public abstract class AbstractTask<V> extends RecursiveTask<V> {
    private final SuspendableFlag suspendFlag;

    public AbstractTask(final SuspendableFlag suspendFlag) {
        this.suspendFlag = suspendFlag;
    }

    @Override
    protected final V compute() {
        suspendFlag.checkIn();
        return computeAbstract();
    }

    protected abstract V computeAbstract();

    protected void debugLog(String msg) {
        Logger.get().debugLog(this.getClass().getSimpleName() + "-" + Thread.currentThread().getName(), msg);
    }
}
