package pcd.ass_single.part1.threads.controller;

import pcd.ass_single.part1.common.Logger;
import pcd.ass_single.part1.common.Parsing;
import pcd.ass_single.part1.common.Flag;
import pcd.ass_single.part1.threads.model.PdfCounterModel;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Pattern;

class PdfCounterAgent implements Runnable {
    private static final Logger LOGGER = Logger.get();
    private final List<File> pdfs;
    private final Pattern regex;
    private final AtomicBoolean stopFlag;
    private final Flag suspendFlag;
    private final PdfCounterModel model;

    public PdfCounterAgent(final List<File> pdfs, final Pattern regex, final PdfCounterModel model,
                           final AtomicBoolean stopFlag, final Flag suspendFlag) {
        this.pdfs = pdfs;
        this.regex = regex;
        this.stopFlag = stopFlag;
        this.suspendFlag = suspendFlag;
        this.model = model;
    }

    @Override
    public void run() {
        for (File pdf : pdfs) {
            suspendFlag.tryAwait();
            if (shouldStop()) {
                break;
            }

            try {
                Parsing.PDFWrapper wrapper = Parsing.loadPdf(pdf);
                if (wrapper.isError()) {
                    debugLog(wrapper.error().orElseThrow());
                } else {
                    var doc = wrapper.document().orElseThrow();
                    if (Parsing.doesPdfSatisfyRegex(doc, regex)) {
                        model.matchingCounter().increment(1);
                    }
                    doc.close();
                }
            } catch (IOException e) {
                debugLog(e.toString());
            }

            model.parsedCounter().increment(1);
        }
        debugLog("finished");
    }

    private boolean shouldStop() {
        return stopFlag.getAcquire();
    }

    private static void debugLog(String msg) {
        LOGGER.debugLog(Thread.currentThread().getName(), msg);
    }
}
