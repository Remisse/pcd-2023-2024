package pcd.ass02.part2.common;

import org.jsoup.nodes.Document;

public class CountOccLib {

	public static int countOccurrences(Document doc, String word) {
		var paragraphs = doc.select("p");
		int count = 0;
		for (var p : paragraphs) {
			String text = p.text();
			String[] words = text.split("[, ?.;:]+");
			for (var w : words) {
				if (w.equals(word)) {
					count++;
				}
			}
		}
		return count;
	}

}
