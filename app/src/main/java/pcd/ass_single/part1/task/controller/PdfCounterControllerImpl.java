package pcd.ass_single.part1.task.controller;

import pcd.ass_single.part1.common.controller.AbstractPdfCounterController;
import pcd.ass_single.part1.common.controller.AgentManager;
import pcd.ass_single.part1.common.model.Model;
import pcd.ass_single.part1.common.view.PdfCounterView;

public final class PdfCounterControllerImpl extends AbstractPdfCounterController<PdfCounterView> {
    private final Model model;
    private final AgentManager agentManager;

    public PdfCounterControllerImpl(final Model model, final AgentManager agentManager) {
        this.model = model;
        this.agentManager = agentManager;
    }

    protected void startComputation() {
        model.reset();
        view().notifyFoundPdfsCount(0);
        view().notifyParsedPdfsCount(0);
        view().notifyTotalPdfsCount(0);
        agentManager.begin(searchDirectory(), searchTerm());
    }

    @Override
    protected void computeAndEnd() {
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

    @Override
    public void setView(PdfCounterView view) {
        super.setView(view);
        agentManager.attachView(view);
    }
}
