package pcd.ass_single.part1.vt.controller.agents;

import pcd.ass_single.part1.common.Directory;
import pcd.ass_single.part1.common.Logger;
import pcd.ass_single.part1.vt.controller.DirectoryContent;
import pcd.ass_single.part1.vt.controller.VTFuture;

public class DirectoryScannerAgent implements Runnable {
    private static final Logger LOGGER = Logger.get();
    private final Directory dir;
    private final VTFuture<DirectoryContent> result;

    public DirectoryScannerAgent(final Directory dir, final VTFuture<DirectoryContent> result) {
        this.dir = dir;
        this.result = result;
    }

    @Override
    public void run() {
        LOGGER.debugLog(Thread.currentThread().getName(), "running");
        try {
            var files = dir.filesOfType("pdf");
            var nestedDirs = dir.nestedDirectories();
            result.set(new DirectoryContent(nestedDirs, files));
        } catch (Exception e) {
            result.setError(e.toString());
        }
    }
}
