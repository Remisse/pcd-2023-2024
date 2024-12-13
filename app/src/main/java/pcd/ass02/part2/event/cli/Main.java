package pcd.ass02.part2.event.cli;

import io.vertx.core.Future;
import pcd.ass02.part2.common.Report;
import pcd.ass02.part2.event.lib.*;

public class Main {

	public static void main(String[] args) throws Exception {
		
		String word = "biblioteca";
		String address = "https://it.wikipedia.org/wiki/Cesena";
		int depth = 2;
		
		System.out.println("Ricerca Parola: " + word + " - indirizzo: " + address + " - profondità: " + depth);
		
		long t0 = System.currentTimeMillis();
		
		Future<Report> fut = WordCountLib.getWordOccurrences(address, word, depth);
		fut.onSuccess((report) -> {
		
			long t1 = System.currentTimeMillis();
	
			System.out.println("Pagine trovate: ");
			
			for (var p: report.getPages()) {
				int occ = report.getOccurrence(p);
				System.out.println("- " + occ + " occorrenze in " + p) ;			
			}
	
			System.out.println("Tempo impiegato: " + (t1 - t0));
		});

	}
}
