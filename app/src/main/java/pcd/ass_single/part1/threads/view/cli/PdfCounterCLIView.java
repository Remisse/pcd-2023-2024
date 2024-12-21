package pcd.ass_single.part1.threads.view.cli;

import pcd.ass_single.part1.common.Logging;
import pcd.ass_single.part1.common.ModelObserver;
import pcd.ass_single.part1.threads.controller.PdfCounterController;
import pcd.ass_single.part1.threads.view.PdfCounterView;

public class PdfCounterCLIView implements PdfCounterView, ModelObserver {
    private final PdfCounterController controller;

    public PdfCounterCLIView(final PdfCounterController controller) {
        this.controller = controller;
    }

    @Override
    public void display() {
        controller.processEvent("Start");
    }

    @Override
    public void notifyTotalPdfsCount(int count) {
        Logging.log("Total: " + count);
    }

    @Override
    public void notifyParsedPdfsCount(int count) {
        //Ops.log("Parsed: " + count);
    }

    @Override
    public void notifyFoundPdfsCount(int count) {
        Logging.log(count + " found");
    }
}
