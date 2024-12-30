package pcd.ass_single.part1.event.view;

import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;
import pcd.ass_single.part1.common.controller.PdfCounterController;
import pcd.ass_single.part1.common.view.PdfCounterView;
import pcd.ass_single.part1.common.view.gui.PdfCounterSwingView;
import pcd.ass_single.part1.event.LocalEventBus;

import java.awt.event.ActionEvent;

public class EventBasedSwingView extends PdfCounterSwingView {
    private static final EventBus BUS = LocalEventBus.get();

    public EventBasedSwingView(PdfCounterController<PdfCounterView> controller) {
        super(controller);
        BUS.consumer("started", m -> {
            notifyTotalPdfsCount(0);
            notifyParsedPdfsCount(0);
            notifyFoundPdfsCount(0);
        });
        BUS.consumer("result", m -> {
            var state = (JsonObject) m.body();
            notifyTotalPdfsCount(state.getInteger("total"));
            notifyParsedPdfsCount(state.getInteger("parsed"));
            notifyFoundPdfsCount(state.getInteger("found"));
        });
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        BUS.publish(ev.getActionCommand(), null);
    }
}
