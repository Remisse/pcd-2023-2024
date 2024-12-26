package pcd.ass_single.part1.vt.controller.agents;

import pcd.ass_single.part1.common.ModelObserver;
import pcd.ass_single.part1.common.model.ConsumableModel;
import pcd.ass_single.part1.common.model.ModelState;

public class ViewUpdater implements Runnable {
    private final ConsumableModel<ModelState> model;
    private final ModelObserver view;

    public ViewUpdater(ConsumableModel<ModelState> model, ModelObserver view) {
        this.model = model;
        this.view = view;
    }

    @Override
    public void run() {
        model.consumeState(s -> {
            view.notifyTotalPdfsCount(s.total());
            view.notifyParsedPdfsCount(s.parsed());
            view.notifyFoundPdfsCount(s.found());
        });
    }
}
