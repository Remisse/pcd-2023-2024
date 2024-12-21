package pcd.ass_single.part1.threads.controller;

import pcd.ass_single.part1.common.Directory;
import pcd.ass_single.part1.threads.view.PdfCounterView;

public interface PdfCounterController {
    void processEvent(String event);

    void setView(PdfCounterView view);

    void setDirectory(Directory dir);

    void setSearchTerm(String word);
}
