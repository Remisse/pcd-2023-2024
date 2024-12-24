package pcd.ass_single.part1.vt.model;

import java.util.function.Consumer;

public interface VTModel {
    void incrementTotal(int n);

    void incrementParsed(int n);

    void incrementFound(int n);

    void consumeResult(Consumer<Result> consumer);

    void reset();
}
