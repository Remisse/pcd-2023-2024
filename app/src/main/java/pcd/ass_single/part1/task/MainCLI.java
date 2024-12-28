package pcd.ass_single.part1.task;

import pcd.ass_single.part1.common.Config;
import pcd.ass_single.part1.common.Directory;
import pcd.ass_single.part1.common.controller.AgentManager;
import pcd.ass_single.part1.common.controller.PdfCounterController;
import pcd.ass_single.part1.common.model.SimpleAtomicModel;
import pcd.ass_single.part1.common.view.PdfCounterView;
import pcd.ass_single.part1.common.view.cli.CLIUtils;
import pcd.ass_single.part1.common.view.cli.PdfCounterCLIView;
import pcd.ass_single.part1.task.controller.AgentManagerImpl;
import pcd.ass_single.part1.task.controller.PdfCounterControllerImpl;

public class MainCLI {
    static {
        Config.setDebugLoggingEnabled(false);
        Config.disablePdfBoxWarnings();
    }

    public static void main(String[] args) {
        if (args.length != 2) {
            CLIUtils.usage();
        }

        SimpleAtomicModel model = new SimpleAtomicModel();
        AgentManager agentManager = new AgentManagerImpl(model, model);
        PdfCounterController<PdfCounterView> controller = new PdfCounterControllerImpl(model, agentManager);
        var view = new PdfCounterCLIView(controller);
        controller.setView(view);
        controller.setDirectory(new Directory(args[0]));
        controller.setSearchTerm(args[1]);
        view.display();
    }
}
