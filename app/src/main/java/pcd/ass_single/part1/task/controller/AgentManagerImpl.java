package pcd.ass_single.part1.task.controller;

import pcd.ass_single.part1.common.Directory;
import pcd.ass_single.part1.common.Flag;
import pcd.ass_single.part1.common.ModelObserver;
import pcd.ass_single.part1.common.Parsing;
import pcd.ass_single.part1.common.controller.AgentManager;
import pcd.ass_single.part1.common.model.ConsumableModel;
import pcd.ass_single.part1.common.model.Model;
import pcd.ass_single.part1.common.model.ModelState;
import pcd.ass_single.part1.task.controller.tasks.DirectoryScanTask;
import pcd.ass_single.part1.task.controller.tasks.ViewUpdateTask;

import java.util.concurrent.*;
import java.util.regex.Pattern;

public class AgentManagerImpl implements AgentManager {
    private final Model model;
    private final ConsumableModel<ModelState> consumableModel;
    private final Flag suspendFlag = new Flag();
    private ScheduledExecutorService scheduledExecutor;
    private ForkJoinPool pool;
    private ForkJoinTask<Void> currentMainTask;
    private ModelObserver view;

    public AgentManagerImpl(Model model, ConsumableModel<ModelState> consumableModel) {
        this.model = model;
        this.consumableModel = consumableModel;
    }

    @Override
    public void begin(Directory startingDirectory, String word) {
        if (view == null) {
            throw new IllegalStateException("View has not been set.");
        }
        model.reset();
        view.notifyFoundPdfsCount(0);
        view.notifyParsedPdfsCount(0);
        view.notifyTotalPdfsCount(0);
        suspendFlag.reset();
        final Pattern regex = Parsing.createRegexOutOfSearchTerm(word);
        pool = new ForkJoinPool();
        currentMainTask = pool.submit(new DirectoryScanTask(model, startingDirectory, regex, suspendFlag));
        scheduledExecutor = Executors.newScheduledThreadPool(1);
        scheduledExecutor.scheduleAtFixedRate(new ViewUpdateTask(consumableModel, view, suspendFlag), 0, 500, TimeUnit.MILLISECONDS);
    }

    @Override
    public void awaitCompletion() {
        try {
            currentMainTask.join();
        } catch (CancellationException ignored) {
        }
        closeAllPools();
        forceViewUpdate();
    }

    private void forceViewUpdate() {
        new ViewUpdateTask(consumableModel, view, suspendFlag).run();
    }

    @Override
    public void stop() {
        closeAllPools();
        resume();
    }

    @Override
    public void suspend() {
        suspendFlag.setToAwait();
    }

    @Override
    public void resume() {
        suspendFlag.reset();
    }

    @Override
    public void attachView(ModelObserver view) {
        this.view = view;
    }

    private void closeAllPools() {
        pool.shutdownNow();
        scheduledExecutor.shutdownNow();
    }
}
