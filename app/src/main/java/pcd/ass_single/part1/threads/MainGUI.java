package pcd.ass_single.part1.threads;

import pcd.ass_single.part1.common.Config;
import pcd.ass_single.part1.threads.controller.PdfCounterControllerImpl;
import pcd.ass_single.part1.threads.model.PdfCounterModelImpl;
import pcd.ass_single.part1.threads.view.gui.PdfCounterSwingView;

public class MainGUI {
    static {
        Config.setDebugLoggingEnabled(false);
        Config.disablePdfBoxWarnings();
    }

    public static void main(String[] args) {
        var model = new PdfCounterModelImpl();
        var controller = new PdfCounterControllerImpl(model, Runtime.getRuntime().availableProcessors() + 1);
        var view = new PdfCounterSwingView(controller);
        model.addObserver(view);
        controller.setView(view);
        view.display();
    }
}
