package pcd.ass_single.part1.event;

import io.vertx.core.Vertx;
import pcd.ass_single.part1.common.Config;
import pcd.ass_single.part1.common.Directory;
import pcd.ass_single.part1.common.view.cli.CLIUtils;
import pcd.ass_single.part1.event.controller.EventBasedController;
import pcd.ass_single.part1.event.view.EventBasedCLIView;

public class MainCLI {
    static {
        Config.setDebugLoggingEnabled(false);
        Config.disablePdfBoxWarnings();
    }

    public static void main(String[] args) {
        if (args.length != 2) {
            CLIUtils.usage();
        }

        final var vertx = Vertx.vertx();
        var controller = new EventBasedController(vertx);
        controller.setDirectory(new Directory(args[0]));
        controller.setSearchTerm(args[1]);
        var view = new EventBasedCLIView(controller);
        controller.setView(view);
        view.display();
    }
}
