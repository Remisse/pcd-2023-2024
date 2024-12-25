package pcd.ass_single.part1.vt.view;

import pcd.ass_single.part1.common.ModelObserver;
import pcd.ass_single.part1.vt.model.VTModel;

public class ViewUpdater implements Runnable {
    private final VTModel model;
    private final ModelObserver view;

    public ViewUpdater(VTModel model, ModelObserver view) {
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
