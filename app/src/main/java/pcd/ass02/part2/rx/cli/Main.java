package pcd.ass02.part2.rx.cli;

import io.reactivex.rxjava3.core.Single;
import pcd.ass02.part2.common.Report;
import pcd.ass02.part2.rx.lib.WordCountLib;

public class Main {

	public static void main(String[] args) throws Exception {
		
		String word = "paese";
		String address = "https://it.wikipedia.org/wiki/Italia";
		int depth = 2;
		
		System.out.println("Ricerca Parola: " + word + " - indirizzo: " + address + " - profondità: " + depth);
		
		long t0 = System.currentTimeMillis();
		
		Single<Report> fut = WordCountLib.getWordOccurrences(address, word, depth);
		fut.subscribe(report -> {
		
			long t1 = System.currentTimeMillis();
	
			System.out.println("Pagine trovate: ");
			
			for (var p: report.getPages()) {
				int occ = report.getOccurrence(p);
				System.out.println("- " + occ + " occorrenze in " + p) ;			
			}
	
			System.out.println("Tempo impiegato: " + (t1 - t0));
		});
		
		Thread.sleep(100000);

	}
}
