package pcd.ass_single.part1.event.verticle;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.json.JsonObject;
import pcd.ass_single.part1.common.controller.PdfCounterController;
import pcd.ass_single.part1.event.LocalEventBus;

public class InputHandler extends AbstractVerticle {
    private static final EventBus BUS = LocalEventBus.get();
    private final PdfCounterController<?> controller;
    private MessageConsumer<JsonObject> inputConsumer;

    public InputHandler(final PdfCounterController<?> controller) {
        this.controller = controller;
    }

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        inputConsumer = BUS.consumer("input", m -> {
            controller.processEvent(m.body().getString("command"));
        });
    }

    @Override
    public void stop() throws Exception {
        inputConsumer.unregister();
    }
}
