package pcd.ass_single.part1.common;

public interface ModelObserver {
    void notifyTotalPdfsCount(int count);

    void notifyParsedPdfsCount(int count);

    void notifyFoundPdfsCount(int count);
}
