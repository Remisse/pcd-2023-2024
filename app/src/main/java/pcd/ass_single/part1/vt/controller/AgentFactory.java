package pcd.ass_single.part1.vt.controller;

import pcd.ass_single.part1.common.Directory;
import pcd.ass_single.part1.common.ModelObserver;
import pcd.ass_single.part1.vt.controller.agents.DirectoryScannerAgent;
import pcd.ass_single.part1.vt.controller.agents.PdfParserAgent;
import pcd.ass_single.part1.vt.model.VTModel;
import pcd.ass_single.part1.vt.view.ViewUpdater;
import scala.Tuple2;

import java.io.File;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Pattern;

public class AgentFactory {
    private final AtomicLong parserId = new AtomicLong();
    private final AtomicLong scannerId = new AtomicLong();
    private final AtomicLong viewUpdaterId = new AtomicLong();

    Tuple2<Thread, VTFuture<DirectoryContent>> createDirectoryScanner(Directory directory) {
        VTFuture<DirectoryContent> result = new VTFutureImpl<>();
        var vt = Thread.ofVirtual()
                .name("DirectoryScanner-" + scannerId.getAndIncrement())
                .start(new DirectoryScannerAgent(directory, result));
        return new Tuple2<>(vt, result);
    }

    Tuple2<Thread, VTFuture<Boolean>> createPdfParser(File pdf, Pattern regex) {
        VTFuture<Boolean> result = new VTFutureImpl<>();
        var vt = Thread.ofVirtual()
                .name("PdfParser-" + parserId.getAndIncrement())
                .start(new PdfParserAgent(pdf, regex, result));
        return new Tuple2<>(vt, result);
    }

    Thread createViewUpdater(VTModel model, ModelObserver view) {
        return Thread.ofVirtual()
                .name("ViewUpdater-" + viewUpdaterId.getAndIncrement())
                .start(new ViewUpdater(model, view));
    }
}
