package pcd.ass_single.part1.common;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.IOException;
import java.util.regex.Pattern;

public class Parsing {
    private Parsing() {}

    public static PDDocument loadPdf(File pdf) throws IOException {
        PDDocument document = PDDocument.load(pdf);
        AccessPermission ap = document.getCurrentAccessPermission();
        if (!ap.canExtractContent()) {
            document.close();
            throw new IllegalStateException("Insufficient permissions to parse \"" + pdf.getAbsolutePath() + " \"");
        }
        return document;
    }

    public static boolean doesPdfSatisfyRegex(PDDocument pdf, Pattern regex) throws IOException {
        PDFTextStripper stripper = new PDFTextStripper();
        stripper.setSortByPosition(true);
        for (int p = 1; p <= pdf.getNumberOfPages(); ++p) {
            stripper.setStartPage(p);
            stripper.setEndPage(p);
            String text = stripper.getText(pdf);
            if (Parsing.containsWord(text, regex)) {
                return true;
            }
        }
        return false;
    }

    public static Pattern createRegexOutOfSearchTerm(final String word) {
        return Pattern.compile(".*\\b" + Pattern.quote(word) + "\\b.*", Pattern.CASE_INSENSITIVE);
    }

    public static boolean containsWord(final String text, final Pattern regex) {
        // Matchers are not thread-safe, but Patterns are
        return regex.matcher(text).find();
    }
}
