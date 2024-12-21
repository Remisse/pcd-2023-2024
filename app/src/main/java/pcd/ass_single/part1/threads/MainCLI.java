package pcd.ass_single.part1.threads;

import pcd.ass_single.part1.common.Config;
import pcd.ass_single.part1.common.Directory;
import pcd.ass_single.part1.common.Logging;
import pcd.ass_single.part1.threads.controller.PdfCounterControllerImpl;
import pcd.ass_single.part1.threads.model.PdfCounterModelImpl;
import pcd.ass_single.part1.threads.view.cli.PdfCounterCLIView;

public class MainCLI {
    static {
        Config.setDebugLoggingEnabled(false);
        Config.disablePdfBoxWarnings();
    }

    public static void main(String[] args) {
        if (args.length != 2) {
           usage();
        }

        var model = new PdfCounterModelImpl();
        var controller = new PdfCounterControllerImpl(model, Runtime.getRuntime().availableProcessors() + 1);
        controller.setDirectory(new Directory(args[0]));
        controller.setSearchTerm(args[1]);
        var view = new PdfCounterCLIView(controller);
        model.addObserver(view);
        controller.setView(view);
        view.display();
    }

    private static void usage() {
        Logging.log("Usage: java <jar-file> <path-to-directory> <search-term>");
        System.exit(-1);
    }
}
