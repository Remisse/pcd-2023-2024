package pcd.ass02.part2.rx.lib;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import pcd.ass02.part2.common.PageInfo;
import pcd.ass02.part2.common.Report;

public class ReportSnapshot implements Report {

	private HashMap<String, PageInfo> map;
	private String word;

	public ReportSnapshot(HashMap<String, PageInfo> map, String word) {
		this.word = word;
		this.map = (HashMap<String, PageInfo>) map.clone();
	}

	public Collection<String> getPages() {
			return  map.keySet();
	}
	
	public int getOccurrence(String page) {
			return map.get(page).nOccurrences();
	}

	public String toString() {
		StringBuffer buf = new StringBuffer();
		for (var e: map.entrySet()) {
			if (e.getValue().nOccurrences() > 0) {
				buf.append("Page: " + e.getKey() + " - occurrences: " + e.getValue().nOccurrences() + "\n"); 
			}
		}
		return buf.toString();
	}	
	
}
