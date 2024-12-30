package pcd.ass_single.part1.event.view;

import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;
import pcd.ass_single.part1.common.controller.PdfCounterController;
import pcd.ass_single.part1.common.view.PdfCounterView;
import pcd.ass_single.part1.common.view.cli.PdfCounterCLIView;
import pcd.ass_single.part1.event.LocalEventBus;

public class EventBasedCLIView extends PdfCounterCLIView {
    private static final EventBus BUS = LocalEventBus.get();

    public EventBasedCLIView(PdfCounterController<PdfCounterView> controller) {
        super(controller);
        BUS.consumer("result", m -> {
            var state = (JsonObject) m.body();
            notifyTotalPdfsCount(state.getInteger("total"));
            notifyParsedPdfsCount(state.getInteger("parsed"));
            notifyFoundPdfsCount(state.getInteger("found"));
        });
        BUS.consumer("stopped", m -> onStop());
    }
}
