package pcd.ass_single.part1.threads.model;

import pcd.ass_single.part1.common.ModelObserver;
import pcd.ass_single.part1.common.Observable;
import pcd.ass_single.part1.common.model.Model;

import java.util.List;

public class ObservableModel implements Model, Observable {
    private final ObservableCounter total = new ObservableCounter(ModelObserver::notifyTotalPdfsCount);
    private final ObservableCounter parsed = new ObservableCounter(ModelObserver::notifyParsedPdfsCount);
    private final ObservableCounter found = new ObservableCounter(ModelObserver::notifyFoundPdfsCount);

    @Override
    public void reset() {
        for (var counter : List.of(total, parsed, found)) {
            counter.reset();
        }
    }

    @Override
    public void addObserver(ModelObserver observer) {
        for (var counter : List.of(total, parsed, found)) {
            counter.addObserver(observer);
        }
    }

    @Override
    public void incrementTotal(int n) {
        total.increment(n);
    }

    @Override
    public void incrementParsed(int n) {
        parsed.increment(n);
    }

    @Override
    public void incrementFound(int n) {
        found.increment(n);
    }
}
