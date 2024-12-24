package pcd.ass_single.part1.vt.controller;

import pcd.ass_single.part1.common.*;
import pcd.ass_single.part1.common.lock.CloseableReentrantLock;
import pcd.ass_single.part1.vt.model.VTModel;
import scala.Tuple2;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Condition;
import java.util.regex.Pattern;

public class AgentManagerImpl implements AgentManager {
    private Pattern regex;
    private final VTModel model;
    private ModelObserver view;
    private final AgentFactory agentFactory = new AgentFactory();
    private final CloseableReentrantLock stopFlag = new CloseableReentrantLock();
    private final Condition stopCondition = stopFlag.newCondition();
    private final AtomicBoolean shouldStop = new AtomicBoolean(false);
    private final SuspendFlag suspendFlag = new SuspendFlag();

    public AgentManagerImpl(final VTModel model) {
        this.model = model;
    }

    @Override
    public void attachView(ModelObserver view) {
        this.view = view;
    }

    @Override
    public void begin(Directory startingDirectory, String searchTerm) {
        this.regex = Parsing.createRegexOutOfSearchTerm(searchTerm);
        model.reset();
        shouldStop.set(false);
        view.notifyTotalPdfsCount(0);
        view.notifyParsedPdfsCount(0);
        view.notifyFoundPdfsCount(0);
        Thread.ofVirtual().start(() -> {
            var vt = scan(startingDirectory);
            if (vt != null) {
                try {
                    vt.join();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            stop();
            try (var ignored = stopFlag.lockAsResource()) {
                stopCondition.signalAll();
            }
        });
    }

    private Thread scan(Directory directory) {
        suspendFlag.tryAwait();
        if (shouldStop()) {
            return null;
        }
        return Thread.ofVirtual()
                .start(() -> {
                    var scannerData = agentFactory.createDirectoryScanner(directory);
                    try {
                        scannerData._1().join();
                        DirectoryContent scannerRes = scannerData._2().get();
                        var scanners = scannerRes.directories().stream()
                                .map(this::scan)
                                .filter(Objects::nonNull)
                                .toList();
                        if (!scannerRes.pdfs().isEmpty()) {
                            model.incrementTotal(scannerRes.pdfs().size());

                            suspendFlag.tryAwait();
                            if (shouldStop()) {
                                return;
                            }

                            List<Tuple2<Thread, Future<Boolean>>> parsers = scannerRes.pdfs().stream()
                                    .map(pdf -> agentFactory.createPdfParser(pdf, regex))
                                    .filter(Objects::nonNull)
                                    .toList();
                            var found = 0;
                            for (var parser : parsers) {
                                parser._1().join();
                                if (parser._2().get()) {
                                    found++;
                                }
                            }
                            boolean parsedSome = !parsers.isEmpty();
                            boolean foundSome = found > 0;
                            if (foundSome) {
                                model.incrementFound(found);
                            }
                            if (parsedSome) {
                                model.incrementParsed(parsers.size());
                            }
                            if (foundSome || parsedSome) {
                                agentFactory.createViewUpdater(model, view).join();
                            }
                        }
                        for (var scanner : scanners) {
                            if (scanner != null) {
                                scanner.join();
                            }
                        }
                    } catch (InterruptedException | ExecutionException e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    @Override
    public void awaitCompletion() throws InterruptedException {
        try (var ignored = stopFlag.lockAsResource()) {
            stopCondition.await();
        }
    }

    @Override
    public void stop() {
        try (var ignored = stopFlag.lockAsResource()) {
            shouldStop.set(true);
        }
        resume();
    }

    @Override
    public void suspend() {
        suspendFlag.suspend();
    }

    @Override
    public void resume() {
        suspendFlag.resume();
    }

    private boolean shouldStop() {
        try {
            stopFlag.lockInterruptibly();
            return shouldStop.get();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            stopFlag.unlock();
        }
    }
}
