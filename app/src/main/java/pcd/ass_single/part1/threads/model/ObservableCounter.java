package pcd.ass_single.part1.threads.model;

import pcd.ass_single.part1.common.ModelObserver;
import pcd.ass_single.part1.common.Observable;
import scala.Int;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class ObservableCounter implements Counter, Observable {
    private int value = 0;
    private final Set<ModelObserver> observers = new HashSet<>();
    private final BiConsumer<ModelObserver, Integer> onNewValue;

    public ObservableCounter(BiConsumer<ModelObserver, Integer> onNewValue) {
        this.onNewValue = Objects.requireNonNull(onNewValue);
    }

    @Override
    public synchronized void addObserver(ModelObserver observer) {
        observers.add(Objects.requireNonNull(observer));
    }

    @Override
    public synchronized void increment(int n) {
        value += n;
        notifyObservers(value);
    }

    @Override
    public synchronized void reset() {
        value = 0;
        notifyObservers(value);
    }

    private void notifyObservers(int val) {
        for (ModelObserver obs : observers) {
            onNewValue.accept(obs, val);
        }
    }
}
