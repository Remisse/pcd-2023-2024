package pcd.ass_single.part1.event.view;

import io.vertx.core.Context;
import io.vertx.core.Promise;
import io.vertx.core.Verticle;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.json.JsonObject;
import pcd.ass_single.part1.common.controller.PdfCounterController;
import pcd.ass_single.part1.common.view.cli.PdfCounterCLIView;
import pcd.ass_single.part1.event.LocalEventBus;

import java.util.List;

public class EventBasedCLIView extends PdfCounterCLIView implements Verticle {
    private static final EventBus BUS = LocalEventBus.get();
    private Vertx vertx;
    private MessageConsumer<JsonObject> resultConsumer;
    private MessageConsumer<?> stoppedConsumer;

    public EventBasedCLIView(PdfCounterController<?> controller) {
        super(controller);
    }

    @Override
    public Vertx getVertx() {
        return vertx;
    }

    @Override
    public void init(Vertx vertx, Context context) {
        this.vertx = vertx;
    }

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        resultConsumer = BUS.consumer("result", m -> {
            JsonObject state = m.body();
            notifyTotalPdfsCount(state.getInteger("total"));
            notifyParsedPdfsCount(state.getInteger("parsed"));
            notifyFoundPdfsCount(state.getInteger("found"));
        });
        stoppedConsumer = BUS.consumer("stopped", m -> onStop());
    }

    @Override
    public void stop(Promise<Void> stopPromise) throws Exception {
        List.of(resultConsumer, stoppedConsumer)
                .forEach(MessageConsumer::unregister);
    }

    @Override
    public void display() {
        BUS.publish("input", new JsonObject().put("command", "Start"));
    }
}
