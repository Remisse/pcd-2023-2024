package pcd.ass_single.part1.task.controller.tasks;

import pcd.ass_single.part1.common.Parsing;
import pcd.ass_single.part1.common.Flag;

import java.io.File;
import java.io.IOException;
import java.util.regex.Pattern;

public class PdfParseTask extends AbstractTask<Boolean> {
    private final File pdf;
    private final Pattern regex;

    public PdfParseTask(final File pdf, final Pattern regex, final Flag suspendFlag) {
        super(suspendFlag);
        this.pdf = pdf;
        this.regex = regex;
    }

    @Override
    protected Boolean computeAbstract() {
        debugLog("running");

        try {
            Parsing.PDFWrapper wrapper = Parsing.loadPdf(pdf);
            if (wrapper.isError()) {
                debugLog(wrapper.error().orElseThrow());
                return false;
            }
            return Parsing.doesPdfSatisfyRegex(wrapper.document().orElseThrow(), regex);
        } catch (IOException e) {
            debugLog(e.toString());
            return false;
        }
    }
}
