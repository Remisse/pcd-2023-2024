package pcd.ass02.part2.event.lib;

import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import pcd.ass02.part2.common.Report;
import pcd.common.Flag;

public class WordCountLib {

	static public Future<Report> getWordOccurrences(String address, String word, int maxDepth) {
		Vertx vertx = Vertx.vertx();
		Promise<Report> prom = Promise.promise();
		Flag stopFlag = new Flag();
		vertx.deployVerticle(new MasterAgent(word, address, maxDepth, prom, stopFlag));
		return prom.future();
	}

	static public Future<Report> getWordOccurrencesDyn(String address, String word, int maxDepth, Flag stopFlag) {
		Vertx vertx = Vertx.vertx();
		Promise<Report> prom = Promise.promise();
		vertx.deployVerticle(new MasterAgent(word, address, maxDepth, prom, stopFlag));
		return prom.future();
	}
	
}
