package pcd.ass_single.part1.threads.controller;

class ComputationState {
    private ComputationStateType current;

    public ComputationState(ComputationStateType type) {
        current = type;
    }

    public synchronized void update(ComputationStateType newState) {
        current = newState;
    }

    public synchronized ComputationStateType get() {
        return current;
    }

    public synchronized void equalsThenAct(ComputationStateType o, Runnable onEquals) {
        while (current == o) {
            onEquals.run();
        }
    }
}
