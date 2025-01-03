package pcd.ass_single.part1.event.view;

import io.vertx.core.Context;
import io.vertx.core.Promise;
import io.vertx.core.Verticle;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.json.JsonObject;
import pcd.ass_single.part1.common.controller.PdfCounterController;
import pcd.ass_single.part1.common.view.gui.PdfCounterSwingView;
import pcd.ass_single.part1.event.LocalEventBus;

import java.awt.event.ActionEvent;
import java.util.List;

public class EventBasedSwingView extends PdfCounterSwingView implements Verticle {
    private static final EventBus BUS = LocalEventBus.get();
    private Vertx vertx;
    private MessageConsumer<?> startedConsumer;
    private MessageConsumer<JsonObject> resultConsumer;

    public EventBasedSwingView(PdfCounterController<Verticle> controller) {
        super(controller);
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        BUS.publish("input", new JsonObject().put("command", ev.getActionCommand()));
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
        startedConsumer = BUS.consumer("started", m -> {
            notifyTotalPdfsCount(0);
            notifyParsedPdfsCount(0);
            notifyFoundPdfsCount(0);
        });
        resultConsumer = BUS.consumer("result", m -> {
            JsonObject state = m.body();
            notifyTotalPdfsCount(state.getInteger("total"));
            notifyParsedPdfsCount(state.getInteger("parsed"));
            notifyFoundPdfsCount(state.getInteger("found"));
        });
    }

    @Override
    public void stop(Promise<Void> stopPromise) throws Exception {
        List.of(startedConsumer, resultConsumer)
                .forEach(MessageConsumer::unregister);
    }
}
