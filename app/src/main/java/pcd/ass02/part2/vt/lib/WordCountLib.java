package pcd.ass02.part2.vt.lib;

import java.util.concurrent.Future;

import pcd.ass02.part2.common.Report;
import pcd.common.Flag;

public class WordCountLib  {
	
	static public FutureWithSnapshots<Report> getWordOccurrences(String address, String word, int maxDepth) {
		TaskManager taskMan = new TaskManager();
		FutureImpl fut = new FutureImpl(taskMan.getSnapshot());
		taskMan.startSearching(word, address, maxDepth, fut);
		return fut;
	}
	
	static public FutureWithSnapshots<Report> getWordOccurrences(String address, String word, int maxDepth, Flag stopFlag) {
		TaskManager taskMan = new TaskManager(stopFlag);
		FutureImpl fut = new FutureImpl(taskMan.getSnapshot());
		taskMan.startSearching(word, address, maxDepth, fut);
		return fut;
	}
}
