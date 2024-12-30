package pcd.ass_single.part1.event.controller;

import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import pcd.ass_single.part1.common.Parsing;
import pcd.ass_single.part1.common.controller.AbstractPdfCounterController;
import pcd.ass_single.part1.common.controller.ComputationStateType;
import pcd.ass_single.part1.common.flag.AtomicFlag;
import pcd.ass_single.part1.common.flag.SuspendableFlag;
import pcd.ass_single.part1.common.view.PdfCounterView;
import pcd.ass_single.part1.event.LocalEventBus;
import pcd.ass_single.part1.event.PdfCounterVerticle;

import java.util.Set;
import java.util.regex.Pattern;

public class EventBasedController extends AbstractPdfCounterController<PdfCounterView> {
    private static final EventBus BUS = LocalEventBus.get();
    private final Vertx vertx;
    private final AtomicFlag stopFlag = new AtomicFlag();
    private final SuspendableFlag suspendFlag = new SuspendableFlag();

    public EventBasedController(final Vertx vertx) {
        this.vertx = vertx;
        vertx.deployVerticle(new InputHandler(this));
    }

    @Override
    public void processEvent(String event) {
        switch (event) {
            case "Start" -> startComputationTemplate();
            case "Stop" -> stopComputationTemplate();
            case "Suspend" -> suspendComputationTemplate();
            case "Resume" -> resumeComputationTemplate();
            default -> throw new IllegalArgumentException("Unhandled event \"" + event + "\".");
        }
    }

    @Override
    protected void startComputation() {
        stopFlag.reset();
        suspendFlag.reset();
        final Pattern regex = Parsing.createRegexOutOfSearchTerm(searchTerm());
        vertx.deployVerticle(new PdfCounterVerticle(searchDirectory(), regex, stopFlag, suspendFlag))
                .onComplete(res -> {
                    setStateAndLog(ComputationStateType.IDLE);
                    BUS.publish("stopped", null);
                });
    }

    @Override
    protected void startComputationTemplate() {
        state().compareThenAct(Set.of(ComputationStateType.IDLE), () -> {
            setStateAndLog(ComputationStateType.STARTING);
        });
        if (state().get() != ComputationStateType.STARTING) {
            return;
        }
        startComputation();
        setStateAndLog(ComputationStateType.RUNNING);
    }

    @Override
    protected void stopComputation() {
        stopFlag.set();
    }

    @Override
    protected void suspendComputation() {
        suspendFlag.set();
    }

    @Override
    protected void resumeComputation() {
        suspendFlag.reset();
    }

    @Override
    protected void doUntilCompletion() {
    }
}
