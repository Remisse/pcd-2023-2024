package pcd.ass_single.part1.threads.model;

import pcd.ass_single.part1.common.ModelObserver;
import pcd.ass_single.part1.common.Observable;
import pcd.ass_single.part1.common.Model;

import java.util.List;

public class PdfCounterModelImpl implements Model, Observable {
    private final ObservableCounter pdfsPending = new ObservableCounter(ModelObserver::notifyTotalPdfsCount);
    private final ObservableCounter pdfsParsed = new ObservableCounter(ModelObserver::notifyParsedPdfsCount);
    private final ObservableCounter pdfsFound = new ObservableCounter(ModelObserver::notifyFoundPdfsCount);

    @Override
    public void reset() {
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

    @Override
    public void incrementTotal(int n) {
        pdfsPending.increment(n);
    }

    @Override
    public void incrementParsed(int n) {
        pdfsParsed.increment(n);
    }

    @Override
    public void incrementFound(int n) {
        pdfsFound.increment(n);
    }
}
