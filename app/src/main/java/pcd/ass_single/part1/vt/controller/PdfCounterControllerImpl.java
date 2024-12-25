package pcd.ass_single.part1.vt.controller;

import pcd.ass_single.part1.common.controller.AbstractPdfCounterController;
import pcd.ass_single.part1.common.controller.AgentManager;
import pcd.ass_single.part1.common.view.PdfCounterView;

public class PdfCounterControllerImpl extends AbstractPdfCounterController<PdfCounterView> {
    private final AgentManager agentManager;

    public PdfCounterControllerImpl(final AgentManager agentManager) {
        this.agentManager = agentManager;
    }

    @Override
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

    @Override
    protected void stopComputation() {
        agentManager.stop();
    }

    @Override
    protected void suspendComputation() {
        agentManager.suspend();
    }

    @Override
    protected void resumeComputation() {
        agentManager.resume();
    }

    @Override
    public void processEvent(String event) {
        new Thread(() -> {
            switch (event) {
                case "Start" -> startComputationTemplate();
                case "Stop" -> stopComputationTemplate();
                case "Suspend" -> suspendComputationTemplate();
                case "Resume" -> resumeComputationTemplate();
                default -> throw new IllegalArgumentException("Unhandled event \"" + event + "\".");
            }
        }).start();
    }

    @Override
    public void setView(PdfCounterView view) {
        super.setView(view);
        agentManager.attachView(view);
    }
}
