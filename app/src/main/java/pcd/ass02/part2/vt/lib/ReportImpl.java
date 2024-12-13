package pcd.ass02.part2.vt.lib;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import pcd.ass02.part2.common.PageInfo;
import pcd.ass02.part2.common.Report;

/**
 * 
 * ReportImpl - implemented as a monitor in this case
 * 
 */
public class ReportImpl implements Report {

	private HashMap<String, PageInfo> map;
	private String word;
	private Lock lock;
	
	public ReportImpl(String word) {
		this.word = word;
		map = new HashMap<>();
		lock = new ReentrantLock();
	}
	
	public void reset() {
		try {
			lock.lock();
			map.clear();
		} finally {
			lock.unlock();
		}
	}

	public void addPageInfo(String pageAddress, int nOcc) {
		try {
			lock.lock();
			map.put(pageAddress, new PageInfo(pageAddress, nOcc));
		} finally {
			lock.unlock();
		}
	}

	public boolean isPagePresent(String pageAddress) {
		try {
			lock.lock();
			return map.containsKey(pageAddress);
		} finally {
			lock.unlock();
		}
	}
	
	public boolean registerPage(String pageAddress) {
		try {
			lock.lock();
			if (!map.containsKey(pageAddress)) {
				map.put(pageAddress, new PageInfo(pageAddress, -1));
				return false;
			} else {
				return true;
			}			
		} finally {
			lock.unlock();
		}
	}
	
	public void polish() {
		try {
			lock.lock();
			var toBeRemoved = new ArrayList<String>();
			for (var e: map.entrySet()) {
				if (e.getValue().nOccurrences() <= 0){
					toBeRemoved.add(e.getKey());
				}
			}
			for (var k: toBeRemoved) {
				map.remove(k);
			}
		} finally {
			lock.unlock();
		}
	}
	
	public Collection<String> getPages() {
		try {
			lock.lock();
			return  map.keySet();
		} catch (Exception ex) {
			return null;
		} finally {
			lock.unlock();
		}
	}
	
	public int getOccurrence(String page) {
		try {
			lock.lock();
			return map.get(page).nOccurrences();
		} finally {
			lock.unlock();
		}
	}

	public String toString() {
		try {
			lock.lock();
			StringBuffer buf = new StringBuffer();
			for (var e: map.entrySet()) {
				if (e.getValue().nOccurrences() > 0) {
					buf.append("Page: " + e.getKey() + " - occurrences: " + e.getValue().nOccurrences() + "\n"); 
				}
			}
			return buf.toString();
		} finally {
			lock.unlock();
		}
	}	
	
}
