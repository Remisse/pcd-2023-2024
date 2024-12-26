package pcd.ass_single.part1.task.controller.tasks;

import pcd.ass_single.part1.common.Flag;
import pcd.ass_single.part1.common.Logger;

import java.util.concurrent.RecursiveTask;

public abstract class AbstractTask<V> extends RecursiveTask<V> {
    private final Flag suspendFlag;

    public AbstractTask(final Flag suspendFlag) {
        this.suspendFlag = suspendFlag;
    }

    @Override
    protected final V compute() {
        suspendFlag.tryAwait();
        return computeAbstract();
    }

    protected abstract V computeAbstract();

    protected void debugLog(String msg) {
        Logger.get().debugLog(this.getClass().getSimpleName() + "-" + Thread.currentThread().getName(), msg);
    }
}
