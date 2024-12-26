package pcd.ass_single.part1.task.controller.tasks;

import pcd.ass_single.part1.common.Directory;
import pcd.ass_single.part1.common.Flag;
import pcd.ass_single.part1.common.Logger;
import pcd.ass_single.part1.common.model.Model;

import java.util.concurrent.ForkJoinTask;
import java.util.regex.Pattern;

public class DirectoryScanTask extends AbstractTask<Void> {
    private static final Logger LOGGER = Logger.get();
    private final Model model;
    private final Directory directory;
    private final Pattern regex;
    private final Flag suspendFlag;

    public DirectoryScanTask(final Model model, final Directory dir, final Pattern regex, final Flag suspendFlag) {
        super(suspendFlag);
        this.model = model;
        directory = dir;
        this.regex = regex;
        this.suspendFlag = suspendFlag;
    }

    @Override
    protected Void computeAbstract() {
        debugLog("running");

        var scanners = directory.nestedDirectories().stream()
                .map(d -> new DirectoryScanTask(model, d, regex, suspendFlag).fork())
                .toList();
        var allPdfs = directory.filesOfType("pdf");
        if (!allPdfs.isEmpty()) {
            model.incrementTotal(allPdfs.size());
            var parsers = allPdfs.stream()
                    .map(pdf -> new PdfParseTask(pdf, regex, suspendFlag).fork())
                    .toList();
            model.incrementParsed(parsers.size());
            int found = (int)parsers.stream()
                    .map(ForkJoinTask::join)
                    .filter(r -> r)
                    .count();
            if (found > 0) {
                model.incrementFound(found);
            }
        }
        scanners.forEach(ForkJoinTask::join);
        return null;
    }
}
