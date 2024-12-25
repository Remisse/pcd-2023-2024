package pcd.ass_single.part1.vt;

import pcd.ass_single.part1.common.Config;
import pcd.ass_single.part1.common.Directory;
import pcd.ass_single.part1.common.controller.PdfCounterController;
import pcd.ass_single.part1.common.view.PdfCounterView;
import pcd.ass_single.part1.common.view.cli.CLIUtils;
import pcd.ass_single.part1.common.view.cli.PdfCounterCLIView;
import pcd.ass_single.part1.common.controller.AgentManager;
import pcd.ass_single.part1.vt.controller.AgentManagerImpl;
import pcd.ass_single.part1.vt.controller.PdfCounterControllerImpl;
import pcd.ass_single.part1.vt.model.VTModel;

public class MainCLI {
    static {
        Config.setDebugLoggingEnabled(true);
        Config.disablePdfBoxWarnings();
    }

    public static void main(String[] args) {
        if (args.length != 2) {
            CLIUtils.usage();
        }

        VTModel model = new VTModel();
        AgentManager agentManager = new AgentManagerImpl(model, model);
        PdfCounterController<PdfCounterView> controller = new PdfCounterControllerImpl(agentManager);
        var view = new PdfCounterCLIView(controller);
        controller.setView(view);
        controller.setDirectory(new Directory(args[0]));
        controller.setSearchTerm(args[1]);
        view.display();
    }
}
