package pcd.ass_single.part1.threads.controller.agents;

import pcd.ass_single.part1.common.*;
import pcd.ass_single.part1.common.model.Model;
import pcd.ass_single.part1.threads.controller.Bag;
import pcd.ass_single.part1.threads.controller.Either;

import java.io.File;
import java.io.IOException;
import java.util.regex.Pattern;

public class PdfCounterAgent implements Runnable {
    private final Model model;
    private final Pattern regex;
    private final Flag suspendFlag;
    private final Bag<Either<Directory, File>> bag;

    public PdfCounterAgent(final Model model, final Bag<Either<Directory, File>> bag, final Pattern regex,
                           final Flag suspendFlag) {
        this.model = model;
        this.regex = regex;
        this.suspendFlag = suspendFlag;
        this.bag = bag;
    }

    @Override
    public void run() {
        while (true) {
            var either = bag.checkInAndAwait();
            if (either.isEmpty()) {
                return;
            }
            either.get().fold(this::actAsScanner, this::actAsParser);
            suspendFlag.tryAwait();
        }
    }

    private void actAsScanner(Directory dir) {
        var directories = dir.nestedDirectories().stream().map(Either::<Directory, File>left).toList();
        var pdfs = dir.filesOfType("pdf").stream().map(Either::<Directory, File>right).toList();
        if (!directories.isEmpty()) {
            bag.putAll(directories);
        }
        if (!pdfs.isEmpty()) {
            model.incrementTotal(pdfs.size());
            bag.putAll(pdfs);
        }
    }

    private void actAsParser(File pdf) {
        try {
            Parsing.PDFWrapper wrapper = Parsing.loadPdf(pdf);
            if (wrapper.isError()) {
                debugLog(wrapper.error().orElseThrow());
            } else {
                var doc = wrapper.document().orElseThrow();
                if (Parsing.doesPdfSatisfyRegex(doc, regex)) {
                    debugLog("found in " + pdf.getPath());
                    model.incrementFound(1);
                }
                doc.close();
            }
            model.incrementParsed(1);
        } catch (IOException e) {
            debugLog(e.toString());
        }
    }

    private static void debugLog(String msg) {
        Logger.get().debugLog(Thread.currentThread().getName(), msg);
    }
}
