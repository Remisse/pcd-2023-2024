package pcd.ass_single.part1.threads.controller;

import pcd.ass_single.part1.common.Directory;
import pcd.ass_single.part1.common.Ops;
import pcd.ass_single.part1.threads.model.PdfCounterModel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

public class PdfCounterControllerImpl implements PdfCounterController {
    private final PdfCounterModel model;
    private Directory searchDirectory = new Directory(new File(".").toURI());
    private String word = "";
    private final int threadCount;
    private CountDownLatch latch;
    private final ComputationState state = new ComputationState(ComputationStateType.IDLE);

    public PdfCounterControllerImpl(final PdfCounterModel m, final int threadCount) {
        model = m;
        this.threadCount = threadCount;
    }

    @Override
    public void setDirectory(Directory dir) {
        searchDirectory = dir;
    }

    @Override
    public void setWord(String word) {
        this.word = word;
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
        final var canContinue = new AtomicBoolean(false);
        state.equalsThenAct(ComputationStateType.IDLE, () -> {
            state.update(ComputationStateType.STARTING);
            canContinue.set(true);
        });
        if (!canContinue.get()) {
            return;
        }

        var allDirectories = new ArrayList<Directory>();
        var pending = new Stack<Directory>();
        pending.addAll(searchDirectory.nestedDirectories());
        while (!pending.empty()) {
            var current = pending.pop();
            allDirectories.add(current);
            var nested = current.nestedDirectories();
            pending.addAll(nested);
            model.pdfsFoundCounter().increment(nested.size());
        }

        final List<File> allPdfs = allDirectories.stream()
                .flatMap(d -> d.filesOfType(".pdf").stream())
                .toList();
        final Pattern regex = Ops.makeWordRegex(word);
        final int actualThreadCount = threadCount + 1;
        latch = new CountDownLatch(actualThreadCount);
        IntStream.range(0, actualThreadCount)
                .mapToObj(i -> {
                    var myStart = (allPdfs.size() * i) / actualThreadCount;
                    var myEnd = (allPdfs.size() * (i + 1)) / actualThreadCount;
                    return new PdfCounterAgent(allPdfs.subList(myStart, myEnd), regex, model, latch, state);
                })
                .forEach(PdfCounterAgent::start);
        state.update(ComputationStateType.RUNNING);
        try {
            latch.await();
            state.update(ComputationStateType.IDLE);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void stopComputation() {
        state.equalsThenAct(ComputationStateType.RUNNING, () -> state.update(ComputationStateType.STOPPING));
    }

    private void suspendComputation() {
        state.equalsThenAct(ComputationStateType.RUNNING, () -> state.update(ComputationStateType.SUSPENDING));
    }

    private void resumeComputation() {
    }
}
