package pcd.ass02.part2.common;

import java.util.Collection;

public interface Report {
	
	Collection<String> getPages();
	
	int getOccurrence(String page); 
}
