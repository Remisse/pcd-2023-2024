package pcd.ass_single.part1.vt.controller;

import pcd.ass_single.part1.common.*;
import pcd.ass_single.part1.common.controller.AgentManager;
import pcd.ass_single.part1.common.model.ConsumableModel;
import pcd.ass_single.part1.common.model.Model;
import pcd.ass_single.part1.common.model.ModelState;
import scala.Tuple2;

import java.io.File;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Pattern;

public class AgentManagerImpl implements AgentManager {
    private static final Logger LOGGER = Logger.get();
    private Pattern regex;
    private final Model model;
    private final ConsumableModel<ModelState> consumableModel;
    private ModelObserver view;
    private final AgentFactory agentFactory = new AgentFactory();
    private final AtomicBoolean stopFlag = new AtomicBoolean();
    private final Flag suspendFlag = new Flag();
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
        this.regex = Parsing.createRegexOutOfSearchTerm(searchTerm);
        model.reset();
        stopFlag.set(false);
        suspendFlag.reset();
        view.notifyTotalPdfsCount(0);
        view.notifyParsedPdfsCount(0);
        view.notifyFoundPdfsCount(0);
        currentManagerThread = Thread.ofVirtual().start(() -> {
            var vt = scan(startingDirectory);
            if (vt != null) {
                try {
                    vt.join();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    private Thread scan(Directory directory) {
        return Thread.ofVirtual()
                .start(() -> {
                    var scannerData = agentFactory.createDirectoryScanner(directory);
                    try {
                        scannerData._1().join();
                        suspendFlag.tryAwait();
                        if (stopFlag.get()) {
                            return;
                        }
                        var future = scannerData._2();
                        if (future.isError()) {
                            LOGGER.debugLog(future.getError());
                            throw new InterruptedException(future.getError());
                        }
                        DirectoryContent scannerRes = future.get();
                        var scanners = scannerRes.directories().stream()
                                .map(this::scan)
                                .filter(Objects::nonNull)
                                .toList();
                        if (!scannerRes.pdfs().isEmpty()) {
                            model.incrementTotal(scannerRes.pdfs().size());
                            runParsers(scannerRes.pdfs());
                            agentFactory.createViewUpdater(consumableModel, view).join();
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

    private void runParsers(List<File> pdfs) throws InterruptedException, ExecutionException {
        List<Tuple2<Thread, VTFuture<Boolean>>> parsers = pdfs.stream()
                .map(pdf -> agentFactory.createPdfParser(pdf, regex))
                .filter(Objects::nonNull)
                .toList();
        if (!parsers.isEmpty()) {
            var found = 0;
            for (var parser : parsers) {
                parser._1().join();
                var future = parser._2();
                if (future.isError()) {
                    LOGGER.debugLog(future.getError());
                } else if (future.get()) {
                    found++;
                }
                suspendFlag.tryAwait();
                if (stopFlag.get()) {
                    return;
                }
            }
            model.incrementParsed(parsers.size());
            if (found > 0) {
                model.incrementFound(found);
            }
        }
    }

    @Override
    public void awaitCompletion() throws InterruptedException {
        currentManagerThread.join();
    }

    @Override
    public void stop() {
        stopFlag.set(true);
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
}
