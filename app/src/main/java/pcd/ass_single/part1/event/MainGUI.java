package pcd.ass_single.part1.event;

import io.vertx.core.Vertx;
import pcd.ass_single.part1.common.Config;
import pcd.ass_single.part1.event.controller.EventBasedController;
import pcd.ass_single.part1.event.view.EventBasedSwingView;

public class MainGUI {
    static {
        Config.setDebugLoggingEnabled(true);
        Config.disablePdfBoxWarnings();
    }

    public static void main(String[] args) {
        final var vertx = Vertx.vertx();
        var controller = new EventBasedController(vertx);
        var view = new EventBasedSwingView(controller);
        controller.setView(view);
        view.display();
    }
}
