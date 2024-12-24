package pcd.ass_single.part1.common.view;

import pcd.ass_single.part1.common.ModelObserver;

public interface PdfCounterView extends ModelObserver {
    void display();

    void onStop();
}
