package pcd.ass_single.part1.common.controller;

import pcd.ass_single.part1.common.Directory;
import pcd.ass_single.part1.common.ModelObserver;

public interface AgentManager {
    void begin(Directory startingDirectory, String word);

    void awaitCompletion() throws InterruptedException;

    void stop();

    void suspend();

    void resume();

    void attachView(ModelObserver view);
}
