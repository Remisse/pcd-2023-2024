package pcd.ass_single.part1.threads;

import pcd.ass_single.part1.common.Config;
import pcd.ass_single.part1.threads.controller.AgentManagerImpl;
import pcd.ass_single.part1.threads.controller.PdfCounterControllerImpl;
import pcd.ass_single.part1.threads.model.ObservableModel;
import pcd.ass_single.part1.common.view.gui.PdfCounterSwingView;

public class MainGUI {
    static {
        Config.setDebugLoggingEnabled(true);
        Config.disablePdfBoxWarnings();
    }

    public static void main(String[] args) {
        var model = new ObservableModel();
        var agentManager = new AgentManagerImpl(model, Runtime.getRuntime().availableProcessors() + 1);
        var controller = new PdfCounterControllerImpl(model, agentManager);
        var view = new PdfCounterSwingView(controller);
        model.addObserver(view);
        controller.setView(view);
        view.display();
    }
}
