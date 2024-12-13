package pcd.ass02.part2.vt.lib;

import java.util.concurrent.Future;

public interface FutureWithSnapshots<T> extends Future<T> {

	T getSnapshot();
}
