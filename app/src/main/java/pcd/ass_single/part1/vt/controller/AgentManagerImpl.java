package pcd.ass_single.part1.vt.controller;

import pcd.ass_single.part1.common.*;
import pcd.ass_single.part1.common.controller.AgentManager;
import pcd.ass_single.part1.common.flag.AtomicFlag;
import pcd.ass_single.part1.common.flag.SuspendableFlag;
import pcd.ass_single.part1.common.model.ConsumableModel;
import pcd.ass_single.part1.common.model.Model;
import pcd.ass_single.part1.common.model.ModelState;
import pcd.ass_single.part1.vt.controller.agents.DirectoryScannerAgent;
import pcd.ass_single.part1.vt.controller.agents.ViewUpdater;

import java.time.Duration;
import java.util.regex.Pattern;

public class AgentManagerImpl implements AgentManager {
    private final Model model;
    private final ConsumableModel<ModelState> consumableModel;
    private ModelObserver view;
    private final AtomicFlag stopFlag = new AtomicFlag();
    private final SuspendableFlag suspendFlag = new SuspendableFlag();
    private Thread currentManagerThread;

    public AgentManagerImpl(final Model model, final ConsumableModel<ModelState> modelConsumer) {
        this.model = model;
        this.consumableModel = modelConsumer;
    }

    @Override
    public void attachView(ModelObserver view) {
        this.view = view;
    }

    @Override
    public void begin(Directory startingDirectory, String searchTerm) {
        if (view == null) {
            throw new IllegalStateException("View not set");
        }
        stopFlag.reset();
        suspendFlag.reset();
        final Pattern regex = Parsing.createRegexOutOfSearchTerm(searchTerm);
        currentManagerThread = Thread.ofVirtual()
                .start(new DirectoryScannerAgent(startingDirectory, regex, model, stopFlag, suspendFlag));
        Thread.ofVirtual()
                .start(() -> {
                    while (!stopFlag.isSet()) {
                        Thread.ofVirtual().start(createViewUpdater());
                        try {
                            Thread.sleep(Duration.ofMillis(500));
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        suspendFlag.checkIn();
                    }
                });
    }

    @Override
    public void awaitCompletion() throws InterruptedException {
        currentManagerThread.join();
        createViewUpdater().run();
        stop();
    }

    @Override
    public void stop() {
        stopFlag.set();
        resume();
    }

    @Override
    public void suspend() {
        suspendFlag.set();
    }

    @Override
    public void resume() {
        suspendFlag.reset();
    }

    private Runnable createViewUpdater() {
        return new ViewUpdater(consumableModel, view);
    }
}
