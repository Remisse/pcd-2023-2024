package pcd.ass_single.part1.event.controller;

import io.vertx.core.Verticle;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import pcd.ass_single.part1.common.Parsing;
import pcd.ass_single.part1.common.controller.AbstractPdfCounterController;
import pcd.ass_single.part1.common.controller.ComputationStateType;
import pcd.ass_single.part1.common.flag.AtomicFlag;
import pcd.ass_single.part1.common.flag.SuspendableFlag;
import pcd.ass_single.part1.event.LocalEventBus;
import pcd.ass_single.part1.event.verticle.InputHandler;
import pcd.ass_single.part1.event.verticle.ParserVerticle;
import pcd.ass_single.part1.event.verticle.ScannerVerticle;

import java.util.List;
import java.util.Set;

public class EventBasedController extends AbstractPdfCounterController<Verticle> {
    private static final EventBus BUS = LocalEventBus.get();
    private final Vertx vertx;
    private final AtomicFlag stopFlag = new AtomicFlag();
    private final SuspendableFlag suspendFlag = new SuspendableFlag();
    private ScannerVerticle scanner;
    private ParserVerticle parser;

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
    protected void startComputationTemplate() {
        state().compareThenAct(Set.of(ComputationStateType.IDLE), () ->
                setStateAndLog(ComputationStateType.STARTING));
        if (state().get() != ComputationStateType.STARTING) {
            return;
        }
        startComputation();
        setStateAndLog(ComputationStateType.RUNNING);
    }

    @Override
    protected void startComputation() {
        stopFlag.reset();
        suspendFlag.reset();
        scanner = new ScannerVerticle(searchDirectory(), stopFlag, suspendFlag);
        parser = new ParserVerticle(Parsing.createRegexOutOfSearchTerm(searchTerm()), stopFlag, suspendFlag);
        deployVerticles();
    }

    @Override
    protected void stopComputation() {
        stopFlag.set();
        suspendFlag.reset();
        undeployVerticles();
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

    private void deployVerticles() {
        vertx.deployVerticle(parser, ar -> {
            BUS.publish("stopped", null);
            setStateAndLog(ComputationStateType.IDLE);
            undeployVerticles();
        });
        vertx.deployVerticle(scanner);
    }

    private void undeployVerticles() {
        List.of(scanner.deploymentID(), parser.deploymentID())
                .forEach(vertx::undeploy);
    }

    @Override
    public void setView(Verticle view) {
        super.setView(view);
        vertx.deployVerticle(view);
    }
}
