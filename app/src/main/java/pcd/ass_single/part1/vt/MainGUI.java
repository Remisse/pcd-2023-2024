package pcd.ass_single.part1.vt;

import pcd.ass_single.part1.common.Config;
import pcd.ass_single.part1.common.controller.PdfCounterController;
import pcd.ass_single.part1.common.view.PdfCounterView;
import pcd.ass_single.part1.common.view.gui.PdfCounterSwingView;
import pcd.ass_single.part1.common.controller.AgentManager;
import pcd.ass_single.part1.vt.controller.AgentManagerImpl;
import pcd.ass_single.part1.vt.controller.PdfCounterControllerImpl;
import pcd.ass_single.part1.common.model.SimpleAtomicModel;

public class MainGUI {
    static {
        Config.setDebugLoggingEnabled(true);
        Config.disablePdfBoxWarnings();
    }

    public static void main(String[] args) {
        SimpleAtomicModel model = new SimpleAtomicModel();
        AgentManager agentManager = new AgentManagerImpl(model, model);
        PdfCounterController<PdfCounterView> controller = new PdfCounterControllerImpl(model, agentManager);
        PdfCounterSwingView view = new PdfCounterSwingView(controller);
        controller.setView(view);
        view.display();
    }
}
