package pcd.ass_single.part1.threads.controller;

import com.google.common.base.Strings;
import pcd.ass_single.part1.common.Directory;
import pcd.ass_single.part1.common.Logging;
import pcd.ass_single.part1.common.Parsing;
import pcd.ass_single.part1.threads.model.PdfCounterModel;
import pcd.ass_single.part1.threads.view.PdfCounterView;

import java.io.File;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

public class PdfCounterControllerImpl implements PdfCounterController {
    private final PdfCounterModel model;
    private PdfCounterView view = null;
    private Directory searchDirectory = new Directory(".");
    private String searchTerm = "";
    private final int threadCount;
    private CountDownLatch latch;
    private AtomicBoolean stopFlag;
    private SuspendFlag suspendFlag;
    private final ComputationState state = new ComputationState(ComputationStateType.IDLE);

    public PdfCounterControllerImpl(final PdfCounterModel m, final int threadCount) {
        model = Objects.requireNonNull(m);
        this.threadCount = threadCount;
    }

    @Override
    public void setView(PdfCounterView view) {
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

    @Override
    public void processEvent(String event) {
        new Thread(() -> {
            switch (event) {
                case "Start"   -> startComputation();
                case "Stop"    -> stopComputation();
                case "Suspend" -> suspendComputation();
                case "Resume"  -> resumeComputation();
                default        -> throw new IllegalArgumentException("Unhandled event \"" + event + "\".");
            }
        }).start();
    }

    private void startComputation() {
        state.compareThenAct(Set.of(ComputationStateType.IDLE), () -> {
            state.update(ComputationStateType.STARTING);
        });
        if (state.get() != ComputationStateType.STARTING) {
            return;
        }

        debugLog("STARTING");
        model.resetAll();

        latch = new CountDownLatch(threadCount);
        stopFlag = new AtomicBoolean(false);
        suspendFlag = new SuspendFlag();
        final List<Directory> allDirectories = getAllDirectoriesRecursively(searchDirectory);
        final List<File> allPdfs = getAllPdfs(allDirectories);
        final Pattern regex = Parsing.createRegexOutOfSearchTerm(searchTerm);
        // Let all threads execute freely, updating the model themselves.
        // A stepped execution like the one implemented for the first assignment would introduce performance
        // bottlenecks, as not all threads will deal with the same amount of work and would thus have to wait for
        // the "slowest" one to complete its step.
        IntStream.range(0, threadCount)
                .mapToObj(i -> {
                    int myStart = (allPdfs.size() * i) / threadCount;
                    int myEnd = (allPdfs.size() * (i + 1)) / threadCount;
                    return new PdfCounterAgent(allPdfs.subList(myStart, myEnd), regex, model, latch, stopFlag, suspendFlag);
                })
                .forEach(Thread::start);
        state.update(ComputationStateType.RUNNING);
        debugLog("RUNNING");

        try {
            latch.await();
            state.update(ComputationStateType.IDLE);
            debugLog("IDLE");
            if (view != null) {
                view.onStop();
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void stopComputation() {
        state.compareThenAct(Set.of(ComputationStateType.RUNNING, ComputationStateType.SUSPENDED), () ->  {
            state.update(ComputationStateType.STOPPING);
            debugLog("STOPPING");
            stopFlag.set(true);
            suspendFlag.resume();
        });
    }

    private void suspendComputation() {
        state.compareThenAct(Set.of(ComputationStateType.RUNNING), () -> {
            state.update(ComputationStateType.SUSPENDING);
            debugLog("SUSPENDING");
            suspendFlag.suspend();
            state.update(ComputationStateType.SUSPENDED);
            debugLog("SUSPENDED");
        });
    }

    private void resumeComputation() {
        state.compareThenAct(Set.of(ComputationStateType.SUSPENDED), () -> {
            state.update(ComputationStateType.RESUMING);
            debugLog("RESUMING");
            suspendFlag.resume();
            state.update(ComputationStateType.RUNNING);
            debugLog("RUNNING");
        });
    }

    private static List<Directory> getAllDirectoriesRecursively(Directory searchDirectory) {
        var allDirectories = new ArrayList<Directory>();
        var pending = new Stack<Directory>();
        pending.addAll(searchDirectory.nestedDirectories());
        while (!pending.empty()) {
            var current = pending.pop();
            allDirectories.add(current);
            var nested = current.nestedDirectories();
            pending.addAll(nested);
        }
        return allDirectories;
    }

    private List<File> getAllPdfs(List<Directory> dirs) {
        return dirs
                .stream()
                .flatMap(d -> {
                    var list = d.filesOfType("pdf");
                    if (!list.isEmpty()) {
                        model.totalCounter().increment(list.size());
                    }
                    return list.stream();
                })
                .toList();
    }

    private static void debugLog(String msg) {
        Logging.debugLog(Thread.currentThread().getName(), msg);
    }
}
