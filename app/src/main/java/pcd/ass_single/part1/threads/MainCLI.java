package pcd.ass_single.part1.threads;

import pcd.ass_single.part1.common.Config;
import pcd.ass_single.part1.common.Directory;
import pcd.ass_single.part1.common.view.cli.CLIUtils;
import pcd.ass_single.part1.threads.controller.AgentManagerImpl;
import pcd.ass_single.part1.threads.controller.PdfCounterControllerImpl;
import pcd.ass_single.part1.threads.model.ObservableModel;
import pcd.ass_single.part1.common.view.cli.PdfCounterCLIView;

public class MainCLI {
    static {
        Config.setDebugLoggingEnabled(false);
        Config.disablePdfBoxWarnings();
    }

    public static void main(String[] args) {
        if (args.length != 2) {
           CLIUtils.usage();
        }

        var model = new ObservableModel();
        var agentManager = new AgentManagerImpl(model, Runtime.getRuntime().availableProcessors() + 1);
        var controller = new PdfCounterControllerImpl(model, agentManager);
        controller.setDirectory(new Directory(args[0]));
        controller.setSearchTerm(args[1]);
        var view = new PdfCounterCLIView(controller);
        model.addObserver(view);
        controller.setView(view);
        view.display();
    }
}
