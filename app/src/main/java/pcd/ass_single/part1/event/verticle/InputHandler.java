package pcd.ass_single.part1.event.verticle;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.MessageConsumer;
import pcd.ass_single.part1.common.controller.PdfCounterController;
import pcd.ass_single.part1.common.view.PdfCounterView;
import pcd.ass_single.part1.event.LocalEventBus;

import java.util.List;

public class InputHandler extends AbstractVerticle {
    private static final EventBus BUS = LocalEventBus.get();
    private final PdfCounterController<PdfCounterView> controller;
    private MessageConsumer<Object> startConsumer;
    private MessageConsumer<Object> stopConsumer;
    private MessageConsumer<Object> suspendConsumer;
    private MessageConsumer<Object> resumeConsumer;

    public InputHandler(final PdfCounterController<PdfCounterView> controller) {
        this.controller = controller;
    }

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        startConsumer = BUS.consumer("Start", m -> controller.processEvent("Start"));
        stopConsumer = BUS.consumer("Stop", m -> controller.processEvent("Stop"));
        suspendConsumer = BUS.consumer("Suspend", m -> controller.processEvent("Suspend"));
        resumeConsumer = BUS.consumer("Resume", m -> controller.processEvent("Resume"));
        startPromise.complete();
    }

    @Override
    public void stop() throws Exception {
        List.of(startConsumer, stopConsumer, suspendConsumer, resumeConsumer)
                .forEach(MessageConsumer::unregister);
        super.stop();
    }
}
