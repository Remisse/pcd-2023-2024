package pcd.ass_single.part1.common.view.cli;

import pcd.ass_single.part1.common.Logger;
import pcd.ass_single.part1.common.controller.PdfCounterController;
import pcd.ass_single.part1.common.view.PdfCounterView;

public class PdfCounterCLIView implements PdfCounterView {
    private static final Logger LOGGER = Logger.get();
    private final PdfCounterController<PdfCounterView> controller;
    private int total = 0;
    private int parsed = 0;
    private int found = 0;

    public PdfCounterCLIView(final PdfCounterController<PdfCounterView> controller) {
        this.controller = controller;
    }

    @Override
    public void display() {
        controller.processEvent("Start");
    }

    @Override
    public void onStop() {
        LOGGER.log("Total: " + total + "\nParsed: " + parsed + "\nContaining search term: " + found);
    }

    @Override
    public void notifyTotalPdfsCount(int count) {
        total = count;
        LOGGER.log("Total: " + count);
    }

    @Override
    public void notifyParsedPdfsCount(int count) {
        parsed = count;
        //Ops.log("Parsed: " + count);
    }

    @Override
    public void notifyFoundPdfsCount(int count) {
        found = count;
        LOGGER.log("Containing search term: " + count);
    }
}
