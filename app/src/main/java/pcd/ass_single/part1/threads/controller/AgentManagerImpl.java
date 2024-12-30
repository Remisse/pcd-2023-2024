package pcd.ass_single.part1.threads.controller;

import pcd.ass_single.part1.common.*;
import pcd.ass_single.part1.common.controller.AgentManager;
import pcd.ass_single.part1.common.flag.SuspendableFlag;
import pcd.ass_single.part1.common.model.Model;
import pcd.ass_single.part1.threads.AtomicBag;
import pcd.ass_single.part1.threads.Bag;
import pcd.ass_single.part1.threads.Either;
import pcd.ass_single.part1.threads.controller.agents.PdfCounterAgent;

import java.io.File;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

public class AgentManagerImpl implements AgentManager {
    private final Model model;
    private final int threadCount;
    private List<Thread> agents;
    private Bag<Either<Directory, File>> bag;
    private final SuspendableFlag suspendFlag = new SuspendableFlag();

    public AgentManagerImpl(final Model model, final int threadCount) {
        this.model = model;
        this.threadCount = threadCount;
    }

    @Override
    public void begin(Directory startingDirectory, String word) {
        final Pattern regex = Parsing.createRegexOutOfSearchTerm(word);
        suspendFlag.reset();
        bag = new AtomicBag<>(threadCount);
        bag.put(Either.left(startingDirectory));
        agents = IntStream.range(0, threadCount)
                .mapToObj(ignored -> new PdfCounterAgent(model, bag, regex, suspendFlag))
                .map(Thread::new)
                .toList();
        agents.forEach(Thread::start);
    }

    @Override
    public void awaitCompletion() throws InterruptedException {
        for (var agent : agents) {
            agent.join();
        }
    }

    @Override
    public void stop() {
        bag.close();
        resume();
    }

    @Override
    public void suspend() {
        suspendFlag.set();
    }

    @Override
    public void resume() {
        suspendFlag.reset();
    }

    @Override
    public void attachView(ModelObserver view) {
        Logger.get().debugLog("View not handled by this agent manager. Ignoring call to attachView()...");
    }
}
