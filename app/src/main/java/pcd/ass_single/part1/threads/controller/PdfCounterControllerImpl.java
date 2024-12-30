package pcd.ass_single.part1.threads.controller;

import pcd.ass_single.part1.common.controller.AbstractPdfCounterController;
import pcd.ass_single.part1.common.model.Model;
import pcd.ass_single.part1.common.view.PdfCounterView;
import pcd.ass_single.part1.common.controller.AgentManager;

public final class PdfCounterControllerImpl extends AbstractPdfCounterController<PdfCounterView> {
    private final Model model;
    private final AgentManager agentManager;

    public PdfCounterControllerImpl(final Model model, final AgentManager agentManager) {
        this.model = model;
        this.agentManager = agentManager;
    }

    protected void startComputation() {
        model.reset();
        agentManager.begin(searchDirectory(), searchTerm());
    }

    @Override
    protected void doUntilCompletion() {
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
