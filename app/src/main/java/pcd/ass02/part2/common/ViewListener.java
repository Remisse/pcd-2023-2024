package pcd.ass02.part2.common;

public interface ViewListener {
	public void notifyStarted(String word, String address, int depth);
	public void notifyStopped();
}
