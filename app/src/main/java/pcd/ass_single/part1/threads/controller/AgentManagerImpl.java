package pcd.ass_single.part1.threads.controller;

import pcd.ass_single.part1.common.*;
import pcd.ass_single.part1.vt.controller.AgentManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

public class AgentManagerImpl implements AgentManager {
    private final Model model;
    private final int threadCount;
    private List<Thread> agents;
    private AtomicBoolean stopFlag;
    private Flag suspendFlag;

    public AgentManagerImpl(final Model model, final int threadCount) {
        this.model = model;
        this.threadCount = threadCount;
    }

    @Override
    public void begin(Directory startingDirectory, String word) {
        model.reset();
        stopFlag = new AtomicBoolean(false);
        suspendFlag = new Flag();
        final List<Directory> allDirectories = getAllDirectoriesRecursively(startingDirectory);
        final List<File> allPdfs = getAllPdfs(allDirectories);
        final Pattern regex = Parsing.createRegexOutOfSearchTerm(word);
        // Let all threads execute freely, updating the model themselves.
        // A stepped execution like the one implemented for the first assignment would introduce performance
        // bottlenecks, as not all threads will deal with the same amount of work and would thus have to wait for
        // the "slowest" one to complete its step.
        agents = IntStream.range(0, threadCount)
                .mapToObj(i -> {
                    int myStart = (allPdfs.size() * i) / threadCount;
                    int myEnd = (allPdfs.size() * (i + 1)) / threadCount;
                    return new PdfCounterAgent(allPdfs.subList(myStart, myEnd), regex, model, stopFlag, suspendFlag);
                })
                .map(Thread::new)
                .toList();
        agents.forEach(Thread::start);
    }

    private static List<Directory> getAllDirectoriesRecursively(Directory searchDirectory) {
        var allDirectories = new ArrayList<Directory>();
        var pending = new Stack<Directory>();
        pending.addAll(searchDirectory.nestedDirectories());
        while (!pending.empty()) {
            var current = pending.pop();
            allDirectories.add(current);
            var nested = current.nestedDirectories();
            pending.addAll(nested);
        }
        return allDirectories;
    }

    private List<File> getAllPdfs(List<Directory> dirs) {
        return dirs
                .stream()
                .flatMap(d -> {
                    var list = d.filesOfType("pdf");
                    if (!list.isEmpty()) {
                        model.incrementTotal(list.size());
                    }
                    return list.stream();
                })
                .toList();
    }

    @Override
    public void awaitCompletion() throws InterruptedException {
        for (var agent : agents) {
            agent.join();
        }
    }

    @Override
    public void stop() {
        stopFlag.set(true);
        resume();
    }

    @Override
    public void suspend() {
        suspendFlag.setToAwait();
    }

    @Override
    public void resume() {
        suspendFlag.reset();
    }

    @Override
    public void attachView(ModelObserver view) {

    }
}
