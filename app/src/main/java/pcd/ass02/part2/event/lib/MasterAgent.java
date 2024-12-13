package pcd.ass02.part2.event.lib;

import java.util.ArrayList;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;
import pcd.ass02.part2.common.*;
import pcd.common.Flag;

public class MasterAgent extends AbstractVerticle {

	private static final int NUM_MAX_LINKS = 20;
	private static final int TIMEOUT = 10000;

	private String word;
	private int maxLevels;
	private String address;
	private Promise<Report> prom;
	private ReportImpl report;
	private Flag stopFlag;

	MasterAgent(String word, String address, int maxLevels, Promise<Report> prom, Flag stopFlag) {
		this.word = word;
		this.address = address;
		this.maxLevels = maxLevels;
		this.prom = prom;
		this.stopFlag = stopFlag;
	}

	public void start(Promise<Void> p) {
		log("started.");
		this.report = new ReportImpl(word);

		searchAndCount(address, 1).onSuccess((v) -> {
			report.polish();
			prom.complete(report);
			p.complete();
		}).onFailure((e) -> {
			prom.fail("stopped");
			p.fail("stopped");
		});

	}

	public Future searchAndCount(String address, int currentLevel) {

		if (stopFlag.isSet()) {
			return Future.failedFuture("stopped");
		}

		log("Fetching page at " + address + " level " + currentLevel);

		var pageFut = asyncFetchPageTask(address);

		var docFut = pageFut.compose((String content) -> {
			log("Level " + currentLevel + " Analysing page " + address);
			return this.asyncParseDocTasks(content);
		});

		var countFut = docFut.compose((Document doc) -> {
			// log("Parsed page " + address + " => counting..");
			return asyncCountOccurrencesTask(doc, word);
		});

		countFut.onSuccess((count) -> {
			log("Level " + currentLevel + " Count done for " + address + " - " + count);
			if (count > 0) {
				report.addPageInfo(address, count);
				JsonObject pageInfo = new JsonObject();
				pageInfo.put("page", address);
				pageInfo.put("numOcc", count);

				EventBus eb = MyEventBus.getInstance(); // this.getVertx().eventBus()
				eb.publish("page-info", pageInfo);
			}
		});

		var linkedPagesFut = docFut.compose((Document doc) -> {
			// log("Parsed page " + address + " => search linked pages..");
			var list = new ArrayList<Future<?>>();
			if (currentLevel < maxLevels) {
				var links = doc.select("a");
				// log("Links: " + links.size());
				int nLinks = 0;
				for (var l : links) {
					if (!stopFlag.isSet()) {
						var addr = l.attr("href");
						if (addr.startsWith("http") || addr.startsWith("https")) {
							boolean alreadyRegistered = report.registerPage(addr);
							if (!alreadyRegistered) {
								if (nLinks < NUM_MAX_LINKS) {
									var f = searchAndCount(addr, currentLevel + 1);
									list.add(f);
									nLinks++;
								}
							}
						}
					} else {
						Future.failedFuture("stopped");
					}
				}
			}
			return Future.all(list);
		});

		return Future.all(countFut, linkedPagesFut);
	}

	/* async tasks, delegated to worker threads */

	private Future<String> asyncFetchPageTask(String uri) {
		Future<String> fut = this.getVertx().executeBlocking(() -> {
			return FetchPageLib.fetchPage(uri, TIMEOUT);
		});
		return fut;
	}

	private Future<Integer> asyncCountOccurrencesTask(Document doc, String word) {
		Future<Integer> fut = this.getVertx().executeBlocking(() -> {
			return CountOccLib.countOccurrences(doc, word);
		});
		return fut;
	}

	private Future<Document> asyncParseDocTasks(String pageContent) {
		Future<Document> fut = this.getVertx().executeBlocking(() -> {
			return Jsoup.parse(pageContent);
		});
		return fut;
	}

	protected void log(String msg) {
		System.out.println("[ TASK MAN AGENT ] " + msg);
	}
}
