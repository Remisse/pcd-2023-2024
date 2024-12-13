package pcd.ass02.part2.event.lib;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import pcd.ass02.part2.common.PageInfo;
import pcd.ass02.part2.common.Report;

public class ReportImpl implements Report {

	private HashMap<String, PageInfo> map;
	private String word;

	public ReportImpl(String word) {
		this.word = word;
		map = new HashMap<>();
	}

	public void reset() {
		map.clear();
	}

	public void addPageInfo(String pageAddress, int nOcc) {
		map.put(pageAddress, new PageInfo(pageAddress, nOcc));
	}

	public boolean isPagePresent(String pageAddress) {
		return map.containsKey(pageAddress);
	}

	public boolean registerPage(String pageAddress) {
		if (!map.containsKey(pageAddress)) {
			map.put(pageAddress, new PageInfo(pageAddress, -1));
			return false;
		} else {
			return true;
		}
	}

	public void polish() {
		var toBeRemoved = new ArrayList<String>();
		for (var e : map.entrySet()) {
			if (e.getValue().nOccurrences() <= 0) {
				toBeRemoved.add(e.getKey());
			}
		}
		for (var k : toBeRemoved) {
			map.remove(k);
		}
	}

	public Collection<String> getPages() {
		return map.keySet();
	}

	public int getOccurrence(String page) {
		return map.get(page).nOccurrences();
	}

	public String toString() {
		StringBuffer buf = new StringBuffer();
		for (var e : map.entrySet()) {
			if (e.getValue().nOccurrences() > 0) {
				buf.append("Page: " + e.getKey() + " - occurrences: " + e.getValue().nOccurrences() + "\n");
			}
		}
		return buf.toString();
	}

}
