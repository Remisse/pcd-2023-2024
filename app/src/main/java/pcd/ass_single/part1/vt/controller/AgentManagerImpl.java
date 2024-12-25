package pcd.ass_single.part1.vt.controller;

import pcd.ass_single.part1.common.*;
import pcd.ass_single.part1.vt.model.VTModel;
import scala.Tuple2;

import java.io.File;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Pattern;

public class AgentManagerImpl implements AgentManager {
    private Pattern regex;
    private final VTModel model;
    private ModelObserver view;
    private final AgentFactory agentFactory = new AgentFactory();
    private final AtomicBoolean stopFlag = new AtomicBoolean();
    private final Flag suspendFlag = new Flag();
    private Thread currentManagerThread;

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
        suspendFlag.tryAwait();
        if (stopFlag.get()) {
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
                            runParsers(scannerRes.pdfs());
                            agentFactory.createViewUpdater(model, view).join();
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
        suspendFlag.tryAwait();
        if (stopFlag.get()) {
            return;
        }
        List<Tuple2<Thread, Future<Boolean>>> parsers = pdfs.stream()
                .map(pdf -> agentFactory.createPdfParser(pdf, regex))
                .filter(Objects::nonNull)
                .toList();
        if (!parsers.isEmpty()) {
            var found = 0;
            for (var parser : parsers) {
                parser._1().join();
                if (parser._2().get()) {
                    found++;
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
