package pcd.ass_single.part1.threads.controller;

import java.util.List;
import java.util.Optional;

public interface Bag<T> {
    void put(T item);

    void putAll(List<T> items);

    Optional<T> checkInAndAwait();

    void close();
}
