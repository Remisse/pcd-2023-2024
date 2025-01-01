package pcd.ass_single.part1.vt.controller.agents;

import pcd.ass_single.part1.common.Directory;
import pcd.ass_single.part1.common.flag.AtomicFlag;
import pcd.ass_single.part1.common.flag.SuspendableFlag;
import pcd.ass_single.part1.common.Logger;
import pcd.ass_single.part1.common.model.Model;
import pcd.ass_single.part1.vt.controller.VTFuture;
import pcd.ass_single.part1.vt.controller.VTFutureImpl;
import scala.Tuple2;

import java.io.File;
import java.util.List;
import java.util.regex.Pattern;

public class DirectoryScannerAgent implements Runnable {
    private static final Logger LOGGER = Logger.get();
    private final Directory dir;
    private final Pattern regex;
    private final Model model;
    private final AtomicFlag stopFlag;
    private final SuspendableFlag suspendFlag;

    public DirectoryScannerAgent(final Directory dir, final Pattern regex, final Model model,
                                 final AtomicFlag stopFlag, final SuspendableFlag suspendFlag) {
        this.dir = dir;
        this.regex = regex;
        this.model = model;
        this.stopFlag = stopFlag;
        this.suspendFlag = suspendFlag;
    }

    @Override
    public void run() {
        suspendFlag.checkIn();
        if (stopFlag.isSet()) {
            return;
        }
        try {
            List<Thread> scanners = dir.nestedDirectories().stream()
                    .map(this::createScanner)
                    .toList();
            var pdfs = dir.filesOfType("pdf");
            if (!pdfs.isEmpty()) {
                model.incrementTotal(pdfs.size());
                List<Tuple2<Thread, VTFuture<Boolean>>> parsers = pdfs.stream()
                        .map(this::createParser)
                        .toList();
                for (var task : parsers) {
                    if (task != null) {
                        task._1().join();
                    }
                }
                model.incrementParsed(parsers.size());
                int found = countMatchingPdfs(parsers.stream().map(Tuple2::_2).toList());
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
        suspendFlag.checkIn();
        if (stopFlag.isSet()) {
            return null;
        }
        VTFuture<Boolean> future = new VTFutureImpl<>();
        var vt = Thread.ofVirtual().start(new PdfParserAgent(pdf, regex, future));
        return new Tuple2<>(vt, future);
    }

    private static int countMatchingPdfs(final List<VTFuture<Boolean>> futures) {
        int found = 0;
        for (var future : futures) {
            if (future.isError()) {
                LOGGER.debugLog(future.getError());
            } else if (future.resultNow()) {
                found++;
            }
        }
        return found;
    }
}
