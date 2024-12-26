package pcd.ass_single.part1.task.controller;

import pcd.ass_single.part1.common.*;
import pcd.ass_single.part1.common.controller.AgentManager;
import pcd.ass_single.part1.common.model.ConsumableModel;
import pcd.ass_single.part1.common.model.Model;
import pcd.ass_single.part1.common.model.ModelState;
import pcd.ass_single.part1.task.controller.tasks.DirectoryScanTask;
import pcd.ass_single.part1.task.controller.tasks.ViewUpdateTask;

import java.util.concurrent.*;
import java.util.regex.Pattern;

public class AgentManagerImpl implements AgentManager {
    private ScheduledExecutorService scheduledExecutor;
    private ForkJoinPool pool;
    private ForkJoinTask<Void> currentMainTask;
    private ScheduledFuture<?> currentViewUpdateTask;
    private final Model model;
    private final ConsumableModel<ModelState> consumableModel;
    private ModelObserver view;
    private final Flag suspendFlag = new Flag();

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
        scheduledExecutor = Executors.newScheduledThreadPool(1);
        currentViewUpdateTask = scheduledExecutor.scheduleWithFixedDelay(new ViewUpdateTask(consumableModel, view, suspendFlag),
                0, 500, TimeUnit.MILLISECONDS);
        currentMainTask = pool.submit(new DirectoryScanTask(model, startingDirectory, regex, suspendFlag));
    }

    @Override
    public void awaitCompletion() {
        currentMainTask.join();
        currentViewUpdateTask.cancel(false);
        forceViewUpdate();
        closeAllPools();
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
        pool.close();
        scheduledExecutor.close();
    }
}
