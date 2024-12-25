package pcd.ass_single.part1.vt.model;

import java.util.function.Consumer;

public interface ConsumableModel<T> {
    void consumeState(Consumer<T> consumer);
}
