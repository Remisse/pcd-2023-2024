package pcd.ass_single.part1.common.controller;

import com.google.common.base.Strings;
import pcd.ass_single.part1.common.Directory;
import pcd.ass_single.part1.common.Logger;

import java.util.*;

public abstract class AbstractPdfCounterController<V> implements PdfCounterController<V> {
    private static final Logger LOGGER = Logger.get();
    private V view;
    private Directory searchDirectory = new Directory(".");
    private String searchTerm = "";
    private final ComputationState state = new ComputationState(ComputationStateType.IDLE);

    @Override
    public void setView(V view) {
        this.view = Objects.requireNonNull(view);
    }

    @Override
    public void setDirectory(Directory dir) {
        searchDirectory = Objects.requireNonNull(dir);
    }

    @Override
    public void setSearchTerm(String term) {
        if (Strings.isNullOrEmpty(term)) {
            throw new IllegalArgumentException("Empty search term");
        }
        this.searchTerm = term;
    }

    protected void startComputationTemplate() {
        state.compareThenAct(Set.of(ComputationStateType.IDLE), () -> {
            setStateAndLog(ComputationStateType.STARTING);
        });
        if (state.get() != ComputationStateType.STARTING) {
            return;
        }
        startComputation();
        setStateAndLog(ComputationStateType.RUNNING);
        doComputationCycle();
        setStateAndLog(ComputationStateType.IDLE);
    }

    protected abstract void startComputation();

    protected abstract void doComputationCycle();

    protected void stopComputationTemplate() {
        state.compareThenAct(Set.of(ComputationStateType.RUNNING, ComputationStateType.SUSPENDED), () ->  {
            setStateAndLog(ComputationStateType.STOPPING);
            stopComputation();
        });
    }

    protected abstract void stopComputation();

    protected void suspendComputationTemplate() {
        state.compareThenAct(Set.of(ComputationStateType.RUNNING), () -> {
            setStateAndLog(ComputationStateType.SUSPENDING);
            suspendComputation();
            setStateAndLog(ComputationStateType.SUSPENDED);
        });
    }

    protected abstract void suspendComputation();

    protected void resumeComputationTemplate() {
        state.compareThenAct(Set.of(ComputationStateType.SUSPENDED), () -> {
            setStateAndLog(ComputationStateType.RESUMING);
            resumeComputation();
            setStateAndLog(ComputationStateType.RUNNING);
        });
    }

    protected abstract void resumeComputation();

    protected static void debugLog(String msg) {
        LOGGER.debugLog(Thread.currentThread().getName(), msg);
    }

    protected V view() {
        return view;
    }

    protected Directory searchDirectory() {
        return searchDirectory;
    }

    protected String searchTerm() {
        return searchTerm;
    }

    private void setStateAndLog(ComputationStateType newState) {
        state.update(newState);
        debugLog(newState.name());
    }
}
