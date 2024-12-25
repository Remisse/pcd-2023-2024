package pcd.ass_single.part1.threads.controller;

import pcd.ass_single.part1.common.controller.AbstractPdfCounterController;
import pcd.ass_single.part1.common.view.PdfCounterView;
import pcd.ass_single.part1.common.controller.AgentManager;

public class PdfCounterControllerImpl extends AbstractPdfCounterController<PdfCounterView> {
    private final AgentManager agentManager;

    public PdfCounterControllerImpl(final AgentManager agentManager) {
        this.agentManager = agentManager;
    }

    @Override
    public void processEvent(String event) {
        new Thread(() -> {
            switch (event) {
                case "Start"   -> startComputationTemplate();
                case "Stop"    -> stopComputationTemplate();
                case "Suspend" -> suspendComputationTemplate();
                case "Resume"  -> resumeComputationTemplate();
                default        -> throw new IllegalArgumentException("Unhandled event \"" + event + "\".");
            }
        }).start();
    }

    protected void startComputation() {
        agentManager.begin(searchDirectory(), searchTerm());
    }

    @Override
    protected void doComputationCycle() {
        try {
            agentManager.awaitCompletion();
            if (view() != null) {
                view().onStop();
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    protected void stopComputation() {
        agentManager.stop();
    }

    protected void suspendComputation() {
        agentManager.suspend();
    }

    protected void resumeComputation() {
        agentManager.resume();
    }
}
