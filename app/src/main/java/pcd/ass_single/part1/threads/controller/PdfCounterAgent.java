package pcd.ass_single.part1.threads.controller;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.apache.pdfbox.text.PDFTextStripper;
import pcd.ass_single.part1.common.Ops;
import pcd.ass_single.part1.threads.model.PdfCounterModel;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.regex.Pattern;

class PdfCounterAgent extends Thread {
    private final List<File> pdfs;
    private final Pattern regex;
    private final CountDownLatch latch;
    private final PdfCounterModel model;
    private final ComputationState state;

    public PdfCounterAgent(final List<File> pdfs, final Pattern regex, final PdfCounterModel model,
                           final CountDownLatch latch, final ComputationState state) {
        this.pdfs = pdfs;
        this.regex = regex;
        this.latch = latch;
        this.model = model;
        this.state = state;
    }

    @Override
    public void run() {
        try {
            for (File pdf : pdfs) {
                if (shouldStop()) {
                    break;
                }
                PDDocument document = PDDocument.load(pdf);
                AccessPermission ap = document.getCurrentAccessPermission();
                if (!ap.canExtractContent()) {
                    throw new IOException("You do not have permission to extract text");
                }
                PDFTextStripper stripper = new PDFTextStripper();
                // This example uses sorting, but in some cases it is more useful to switch it off,
                // e.g. in some files with columns where the PDF content stream respects the
                // column order.
                stripper.setSortByPosition(true);
                var wordFound = false;
                for (int p = 1; p <= document.getNumberOfPages() && !wordFound; ++p) {
                    // Set the page interval to extract. If you don't, then all pages would be extracted.
                    stripper.setStartPage(p);
                    stripper.setEndPage(p);
                    // let the magic happen
                    String text = stripper.getText(document);
                    if (Ops.findWord(text, regex)) {
                        model.pdfsMatchingCounter().increment(1);
                        wordFound = true;
                    }
                    // If the extracted text is empty or gibberish, please try extracting text
                    // with Adobe Reader first before asking for help. Also read the FAQ
                    // on the website:
                    // https://pdfbox.apache.org/2.0/faq.html#text-extraction
                }
                model.pdfsParsedCounter().increment(1);
                document.close();
            }
            latch.countDown();
        } catch (IOException ignored) {
        }
    }

    private boolean shouldStop() {
        return state.get() == ComputationStateType.STOPPING;
    }
}
