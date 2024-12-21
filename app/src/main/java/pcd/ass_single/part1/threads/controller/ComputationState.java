package pcd.ass_single.part1.threads.controller;

import java.util.Set;

class ComputationState {
    private ComputationStateType current;

    public ComputationState(ComputationStateType type) {
        current = type;
    }

    public synchronized void update(ComputationStateType newState) {
        current = newState;
    }

    public synchronized void compareThenAct(Set<ComputationStateType> toCompare, Runnable onEquals) {
        while (toCompare.contains(current)) {
            onEquals.run();
        }
    }

    public synchronized ComputationStateType get() {
        return current;
    }
}
