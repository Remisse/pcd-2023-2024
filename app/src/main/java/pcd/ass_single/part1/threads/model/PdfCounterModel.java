package pcd.ass_single.part1.threads.model;

public interface PdfCounterModel {
    ObservableCounter pdfsFoundCounter();

    ObservableCounter pdfsParsedCounter();

    ObservableCounter pdfsMatchingCounter();
}
