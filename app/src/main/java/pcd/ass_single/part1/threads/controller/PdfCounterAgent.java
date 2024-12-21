package pcd.ass_single.part1.threads.controller;

import org.apache.pdfbox.pdmodel.PDDocument;
import pcd.ass_single.part1.common.Parsing;
import pcd.ass_single.part1.threads.model.PdfCounterModel;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Pattern;

import static pcd.ass_single.part1.common.Logging.debugLog;

class PdfCounterAgent extends Thread {
    private final List<File> pdfs;
    private final Pattern regex;
    private final CountDownLatch latch;
    private final AtomicBoolean stopFlag;
    private final SuspendFlag suspendFlag;
    private final PdfCounterModel model;

    public PdfCounterAgent(final List<File> pdfs, final Pattern regex, final PdfCounterModel model,
                           final CountDownLatch latch, final AtomicBoolean stopFlag, final SuspendFlag suspendFlag) {
        this.pdfs = pdfs;
        this.regex = regex;
        this.latch = latch;
        this.stopFlag = stopFlag;
        this.suspendFlag = suspendFlag;
        this.model = model;
    }

    @Override
    public void run() {
        try {
            for (File pdf : pdfs) {
                suspendFlag.tryAwait();
                if (shouldStop()) {
                    break;
                }

                PDDocument document = Parsing.loadPdf(pdf);
                if (Parsing.doesPdfSatisfyRegex(document, regex)) {
                    model.matchingCounter().increment(1);
                }
                closeDocument(document);
            }
            debugLog(Thread.currentThread().getName(), "finished");
        } catch (IllegalStateException e) {
            debugLog(Thread.currentThread().getName(), "" + e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            latch.countDown();
        }
    }

    private void closeDocument(PDDocument doc) throws IOException {
        model.parsedCounter().increment(1);
        doc.close();
    }

    private boolean shouldStop() {
        return stopFlag.getAcquire();
    }
}
