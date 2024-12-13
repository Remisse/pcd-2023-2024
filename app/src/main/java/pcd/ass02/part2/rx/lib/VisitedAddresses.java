package pcd.ass02.part2.rx.lib;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import pcd.ass02.part2.common.PageInfo;
import pcd.ass02.part2.common.Report;

public class VisitedAddresses {

	private HashMap<String, String> map;
	private Lock lock;
	
	public VisitedAddresses() {
		map = new HashMap<>();
		lock = new ReentrantLock();
	}
	
	public boolean registerAddress(String address) {
		try {
			lock.lock();
			// System.out.print("Registering... " + address);
			if (!map.containsKey(address)) {
				// System.out.println(" - NEW");
				map.put(address, address);
				return false;
			} else {
				// System.out.println(" - ALREADY REG");
				return true;
			}			
		} finally {
			lock.unlock();
		}
	}
	

}
