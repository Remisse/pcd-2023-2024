package pcd.ass_single.part1.threads.controller;

import pcd.ass_single.part1.common.Directory;

public interface PdfCounterController {
    void processEvent(String event);

    void setDirectory(Directory dir);

    void setSearchTerm(String word);
}
