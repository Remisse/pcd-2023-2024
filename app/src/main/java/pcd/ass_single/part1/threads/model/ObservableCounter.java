package pcd.ass_single.part1.threads.model;

import pcd.ass_single.part1.common.ModelObserver;
import pcd.ass_single.part1.common.Observable;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

public class ObservableCounter implements Counter, Observable {
    private int value = 0;
    private final Set<ModelObserver> observers = new HashSet<>();
    private final Consumer<ModelObserver> onIncrement;

    public ObservableCounter(Consumer<ModelObserver> onIncrement) {
        this.onIncrement = onIncrement;
    }

    @Override
    public synchronized void addObserver(ModelObserver observer) {
        Objects.requireNonNull(observer);
        observers.add(observer);
    }

    @Override
    public synchronized void increment(int n) {
        value += n;
        for (ModelObserver obs : observers) {
            onIncrement.accept(obs);
        }
    }
}
