package pcd.ass_single.part1.vt.controller;

import java.util.concurrent.Future;

public interface VTFuture<T> extends Future<T> {
    void set(T value);
}
