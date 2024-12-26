package pcd.ass_single.part1.task.controller.tasks;

import pcd.ass_single.part1.common.Flag;
import pcd.ass_single.part1.common.Logger;
import pcd.ass_single.part1.common.ModelObserver;
import pcd.ass_single.part1.common.model.ConsumableModel;
import pcd.ass_single.part1.common.model.ModelState;

public class ViewUpdateTask implements Runnable {
    private final ConsumableModel<ModelState> model;
    private final ModelObserver view;
    private final Flag suspendFlag;

    public ViewUpdateTask(ConsumableModel<ModelState> model, ModelObserver view, Flag suspendFlag) {
        this.model = model;
        this.view = view;
        this.suspendFlag = suspendFlag;
    }

    @Override
    public void run() {
        suspendFlag.tryAwait();
        Logger.get().debugLog("ViewUpdater-" + Thread.currentThread().getName(), "running");
        model.consumeState(s -> {
            view.notifyTotalPdfsCount(s.total());
            view.notifyParsedPdfsCount(s.parsed());
            view.notifyFoundPdfsCount(s.found());
        });
    }
}
