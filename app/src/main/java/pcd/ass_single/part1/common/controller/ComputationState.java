package pcd.ass_single.part1.common.controller;

import pcd.ass_single.part1.common.lock.CloseableReentrantLock;

import java.util.Set;

public class ComputationState {
    private ComputationStateType current;
    private final CloseableReentrantLock lock = new CloseableReentrantLock();

    public ComputationState(ComputationStateType type) {
        current = type;
    }

    public void update(ComputationStateType newState) {
        try (var ignored = lock.lockAsResource()) {
            current = newState;
        }
    }

    public void compareThenAct(Set<ComputationStateType> toCompare, Runnable onEquals) {
        try (var ignored = lock.lockAsResource()) {
            while (toCompare.contains(current)) {
                onEquals.run();
            }
        }
    }

    public ComputationStateType get() {
        try (var ignored = lock.lockAsResource()) {
            return current;
        }
    }
}
