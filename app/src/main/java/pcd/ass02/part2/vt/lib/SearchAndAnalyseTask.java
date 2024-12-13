package pcd.ass02.part2.vt.lib;

import java.net.URI;
import java.net.http.*;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import pcd.ass02.part2.common.CountOccLib;
import pcd.ass02.part2.common.FetchPageLib;
import pcd.ass02.part2.common.PageFetchException;

public class SearchAndAnalyseTask implements Runnable {

	private String address;
	private String word;
	private int currLevel;
	private int maxLevel;
	private TaskManager man;
	private ReportImpl pages;
	private static int TIMEOUT = 10000;
	
	public SearchAndAnalyseTask(String word, String address, int currLevel, int maxLevel, ReportImpl pages, TaskManager man) {
		this.address = address;
		this.word = word;
		this.currLevel = currLevel;
		this.maxLevel = maxLevel;
		this.man = man;
		this.pages = pages;
		// log("New search task " + address);
	}

	public void run() {
		try {
			// log("Fetching page at " + address);
			String pageContent = FetchPageLib.fetchPage(address, TIMEOUT);
			
			Document doc = Jsoup.parse(pageContent);
			// log("Doc parsed - " + doc.title());

			List<Thread> tasks = new ArrayList<Thread>();
			if (currLevel < maxLevel) {
				var links = doc.select("a");
				// log("Num links: " + links.size());

				for (var l: links) {
					var addr = l.attr("href");
					if (addr.startsWith("http") || addr.startsWith("https")) {
						try {
							var t = man.spawnSearchTask(addr, currLevel + 1);
							tasks.add(t);
						} catch (Exception ex) {
						}
					}
				}
			}

			int nOcc = CountOccLib.countOccurrences(doc, word);
			// log("Occurrences: " + nOcc);
			if (nOcc > 0){
				pages.addPageInfo(address, nOcc);
			}
			
			// log("Tasks to wait: " + tasks.size());
			for (var t: tasks) {
				try {
					t.join();
				} catch (Exception ex) {}
			}
			// log("DONE - remaining " + man.getOngoingTasks());
		} catch (Exception ex) {
			// log("Error in fetching page at " + address + " - IGNORING");
			// ex.printStackTrace();
		} finally {
			man.taskDone(Thread.currentThread().getName());			
		}
	}
		
	protected void log(String msg) {
		System.out.println("[" + Thread.currentThread().getName() + "] " + msg);
	}
}
