package pcd.ass_single.part1.common.controller;

import pcd.ass_single.part1.common.Directory;

public interface PdfCounterController<V> {
    void processEvent(String event);

    void setView(V view);

    void setDirectory(Directory dir);

    void setSearchTerm(String word);
}
