package pcd.ass_single.part1.event.controller;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.EventBus;
import pcd.ass_single.part1.common.controller.PdfCounterController;
import pcd.ass_single.part1.common.view.PdfCounterView;
import pcd.ass_single.part1.event.LocalEventBus;

public class InputHandler extends AbstractVerticle {
    private static final EventBus BUS = LocalEventBus.get();

    public InputHandler(final PdfCounterController<PdfCounterView> controller) {
        BUS.consumer("Start", m -> controller.processEvent("Start"));
        BUS.consumer("Stop", m -> controller.processEvent("Stop"));
        BUS.consumer("Suspend", m -> controller.processEvent("Suspend"));
        BUS.consumer("Resume", m -> controller.processEvent("Resume"));
    }

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        startPromise.complete();
    }
}
