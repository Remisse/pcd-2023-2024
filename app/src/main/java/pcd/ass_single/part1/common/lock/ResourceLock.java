package pcd.ass_single.part1.common.lock;

public interface ResourceLock extends AutoCloseable {
    @Override
    void close();
}
