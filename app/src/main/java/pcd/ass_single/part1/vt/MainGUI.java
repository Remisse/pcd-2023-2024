package pcd.ass_single.part1.vt;

import pcd.ass_single.part1.common.Config;
import pcd.ass_single.part1.common.controller.PdfCounterController;
import pcd.ass_single.part1.common.view.PdfCounterView;
import pcd.ass_single.part1.common.view.gui.PdfCounterSwingView;
import pcd.ass_single.part1.vt.controller.AgentManager;
import pcd.ass_single.part1.vt.controller.AgentManagerImpl;
import pcd.ass_single.part1.vt.controller.PdfCounterControllerImpl;
import pcd.ass_single.part1.vt.model.VTModel;
import pcd.ass_single.part1.vt.model.VTModelImpl;

public class MainGUI {
    static {
        Config.setDebugLoggingEnabled(true);
        Config.disablePdfBoxWarnings();
    }

    public static void main(String[] args) {
        VTModel model = new VTModelImpl();
        AgentManager agentManager = new AgentManagerImpl(model);
        PdfCounterController<PdfCounterView> controller = new PdfCounterControllerImpl(agentManager);
        PdfCounterSwingView view = new PdfCounterSwingView(controller);
        controller.setView(view);
        view.display();
    }
}
