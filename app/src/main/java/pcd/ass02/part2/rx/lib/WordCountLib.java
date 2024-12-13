package pcd.ass02.part2.rx.lib;

import java.util.Optional;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;
import pcd.ass02.part2.common.CountOccLib;
import pcd.ass02.part2.common.FetchPageLib;
import pcd.ass02.part2.common.PageInfo;
import pcd.ass02.part2.common.Report;
import pcd.common.Flag;

public class WordCountLib  {

	static final int TIMEOUT = 10000;
	static final int NUM_MAX_LINKS = 20;
	
	static public Single<Report> getWordOccurrences(String address, String word, int maxDepth) {
		ReportImpl report = new ReportImpl(word);
		return getWordOccurrencesDyn(address, word, maxDepth, Optional.empty()).last(report);
	}

	static public Flowable<Report> getWordOccurrencesDyn(String address, String word, int maxDepth, Optional<Flag> stoppedFlag) {
		var visitedAddresses = new VisitedAddresses();
		
		try {
			/* starting from the stream of documents */ 
			
			var docStream = genDocStream(new AddressWithLevel(address,1), word, maxDepth, visitedAddresses, stoppedFlag);
	
			/* generating a stream of page info */
			
			var pageInfoStream = 
					docStream
					.subscribeOn(Schedulers.io())
					.map(docWithLevel -> {
						int nOcc = CountOccLib.countOccurrences(docWithLevel.doc(), word);
						return new PageInfo(docWithLevel.address(), nOcc);
					});
			
			/* generating a stream of report snapshots */
			
			ReportImpl report = new ReportImpl(word);		
			Flowable<Report> reportSnapshots =
				pageInfoStream
				.filter(pageInfo -> {
					return pageInfo.nOccurrences() > 0;
				})
				.map(pageInfo -> {
					report.addPageInfo(pageInfo.address(), pageInfo.nOccurrences());
					return report.getSnapshot();
				});
			
			return reportSnapshots;
		} catch (Exception ex) {
			return Flowable.empty();
		}
	}
	//
	
	static private Flowable<DocWithLevel> genDocStream(AddressWithLevel l, String word, int maxLevels, VisitedAddresses visited, Optional<Flag> stopFlag) throws StoppedException {
		
		if (stopFlag.isPresent() && stopFlag.get().isSet()) {
			throw new StoppedException(); 
		}
		int level = l.level();
		String address = l.address();
		try {

			log("Level " + level + " - Fetching " + address + " ... ");
			String content =  FetchPageLib.fetchPage(address, TIMEOUT);
			
			log("Level " + level + " - Parsing " + address + " ... ");
			Document doc = Jsoup.parse(content);
			visited.registerAddress(address);
			
			log("Level " + level + " page: " + address + " parsed. ");
			
			var docsFlat = Flowable.just(new DocWithLevel(address, doc, level));
			
			if (level < maxLevels) {
				var links = doc.select("a");
				log("Links " + links.size());
				var linkedDocsStream = 
					Flowable
					.fromArray(links.toArray())
					.filter(obj -> {
						Element link = (Element) obj;
						var addr = link.attr("href");
						return (addr.startsWith("http")) || (addr.startsWith("https"));
					})
					.filter(obj -> {
						Element link = (Element) obj;
						var addr = link.attr("href");
						boolean alreadyRegistered = visited.registerAddress(addr);
						return !alreadyRegistered;
					})
					.map(obj -> {
						Element link = (Element) obj;
						// log("Level " + l.level()+ " - extracted link " + link.attr("href") + " page: " + address);
						return new AddressWithLevel(link.attr("href"), level);
					})
					.take(NUM_MAX_LINKS)
					.flatMap(addrWithLevel -> {
						return genDocStream(new AddressWithLevel(addrWithLevel.address(), level + 1), word, maxLevels, visited, stopFlag);
					});
					// .subscribeOn(Schedulers.io());

				return Flowable
						.merge(docsFlat, linkedDocsStream);

			} else {
				return docsFlat;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return Flowable.empty();
	}
	
	static void log(String msg) {
		System.out.println("[" + Thread.currentThread()+ "] " + msg);
	}
}
