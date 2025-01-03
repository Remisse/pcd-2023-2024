package pcd.ass_single.part1.vt.controller.agents;

import pcd.ass_single.part1.common.Parsing;
import pcd.ass_single.part1.vt.controller.VTFuture;

import java.io.File;
import java.io.IOException;
import java.util.regex.Pattern;

public class PdfParserAgent implements Runnable {
    private final File pdf;
    private final Pattern regex;
    private final VTFuture<Boolean> result;

    public PdfParserAgent(final File pdf, final Pattern regex, final VTFuture<Boolean> result) {
        this.pdf = pdf;
        this.regex = regex;
        this.result = result;
    }

    @Override
    public void run() {
        try {
            Parsing.PDFWrapper wrapper = Parsing.loadPdf(pdf);
            if (wrapper.isError()) {
                result.setError(wrapper.error().orElseThrow());
            } else {
                result.set(Parsing.doesPdfSatisfyRegex(wrapper.document().orElseThrow(), regex));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
