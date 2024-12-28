package pcd.ass_single.part1.vt.controller.agents;

import pcd.ass_single.part1.common.Directory;
import pcd.ass_single.part1.common.Flag;
import pcd.ass_single.part1.common.Logger;
import pcd.ass_single.part1.common.model.Model;
import pcd.ass_single.part1.vt.controller.VTFuture;
import pcd.ass_single.part1.vt.controller.VTFutureImpl;
import scala.Tuple2;

import java.io.File;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Pattern;

public class DirectoryScannerAgent implements Runnable {
    private static final Logger LOGGER = Logger.get();
    private final Directory dir;
    private final Pattern regex;
    private final Model model;
    private final AtomicBoolean stopFlag;
    private final Flag suspendFlag;

    public DirectoryScannerAgent(final Directory dir, final Pattern regex, final Model model,
                                 final AtomicBoolean stopFlag, final Flag suspendFlag) {
        this.dir = dir;
        this.regex = regex;
        this.model = model;
        this.stopFlag = stopFlag;
        this.suspendFlag = suspendFlag;
    }

    @Override
    public void run() {
        suspendFlag.tryAwait();
        if (stopFlag.get()) {
            return;
        }
        try {
            var scanners = dir.nestedDirectories().stream()
                    .map(this::createScanner)
                    .toList();

            var pdfs = dir.filesOfType("pdf");
            if (!pdfs.isEmpty()) {
                model.incrementTotal(pdfs.size());
                var parsers = pdfs.stream()
                        .map(this::createParser)
                        .toList();
                for (var task : parsers) {
                    task._1().join();
                }
                int found = 0;
                for (var tuple : parsers) {
                    if (tuple._2().isError()) {
                        LOGGER.debugLog(tuple._2().getError());
                    } else if (tuple._2().resultNow()) {
                        found++;
                    }
                }
                if (!parsers.isEmpty()) {
                    model.incrementParsed(parsers.size());
                }
                if (found > 0) {
                    model.incrementFound(found);
                }
            }
            for (var scanner : scanners)  {
                scanner.join();
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private Thread createScanner(Directory dir) {
        return Thread.ofVirtual().start(new DirectoryScannerAgent(dir, regex, model, stopFlag, suspendFlag));
    }

    private Tuple2<Thread, VTFuture<Boolean>> createParser(File pdf) {
        VTFuture<Boolean> future = new VTFutureImpl<>();
        var vt = Thread.ofVirtual().start(new PdfParserAgent(pdf, regex, future));
        return new Tuple2<>(vt, future);
    }
}
