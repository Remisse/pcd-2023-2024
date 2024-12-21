package pcd.ass_single.part1.threads.model;

import pcd.ass_single.part1.common.ModelObserver;
import pcd.ass_single.part1.common.Observable;

public interface PdfCounterModel extends Observable {
    Counter totalCounter();

    Counter parsedCounter();

    Counter matchingCounter();

    void resetAll();

    void addObserver(ModelObserver observer);
}
