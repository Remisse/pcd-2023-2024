package pcd.ass_single.part1.threads.model;

import pcd.ass_single.part1.common.ModelObserver;

import java.util.List;

public class PdfCounterModelImpl implements PdfCounterModel {
    private final ObservableCounter pdfsPending = new ObservableCounter(ModelObserver::notifyTotalPdfsCount);
    private final ObservableCounter pdfsParsed = new ObservableCounter(ModelObserver::notifyParsedPdfsCount);
    private final ObservableCounter pdfsFound = new ObservableCounter(ModelObserver::notifyFoundPdfsCount);

    @Override
    public ObservableCounter totalCounter() {
        return pdfsPending;
    }

    @Override
    public ObservableCounter parsedCounter() {
        return pdfsParsed;
    }

    @Override
    public ObservableCounter matchingCounter() {
        return pdfsFound;
    }

    @Override
    public void resetAll() {
        for (var counter : List.of(pdfsPending, pdfsParsed, pdfsFound)) {
            counter.reset();
        }
    }

    @Override
    public void addObserver(ModelObserver observer) {
        for (var counter : List.of(pdfsPending, pdfsParsed, pdfsFound)) {
            counter.addObserver(observer);
        }
    }
}
