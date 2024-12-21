package pcd.ass_single.part1.common;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.regex.Pattern;

public class Parsing {
    public record PDFWrapper(Optional<PDDocument> document, Optional<String> error) {
        public boolean isError() {
            return error.isPresent();
        }
    }

    private Parsing() {}

    public static PDFWrapper loadPdf(File pdf) throws IOException {
        PDDocument document = PDDocument.load(pdf);
        AccessPermission ap = document.getCurrentAccessPermission();
        if (!ap.canExtractContent()) {
            document.close();
            return new PDFWrapper(
                    Optional.empty(),
                    Optional.of("Insufficient permissions to parse \"" + pdf.getName() + "\"")
            );
        }
        return new PDFWrapper(Optional.of(document), Optional.empty());
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
