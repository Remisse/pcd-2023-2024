package pcd.ass_single.part1.common;

public interface ModelObserver {
    void notifyPendingPdfsCount(int count);

    void notifyScannedPdfsCount(int count);

    void notifyRelevantPdfsCount(int count);
}
