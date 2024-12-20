package pcd.lab07;

import io.vertx.core.*;


/**
 * Exercise
 * 
 * -- implement an async protected method getDelayedRandom(int delay)
 *    that returns a random value between 0 and 1 after the specified
 *    amount of time
 *    
 * -- in the "start" method, test the behaviour of the method by using it.
 * 
 */
class VerticleWithPromise extends AbstractVerticle {
	public void start() {
		log("started.");
		Future<Double> result = getDelayedRandom(1000);
		result.onSuccess(res -> log("result: " + res));
	}

	protected Future<Double> getDelayedRandom(int delay) {
		Promise<Double> p = Promise.promise();
		getVertx().setTimer(delay, x -> {
			var r = Math.random();
			p.complete(r);
		});
		return p.future();
	}

	private void log(String msg) {
		System.out.println("[REACTIVE AGENT]["+Thread.currentThread()+"]" + msg);
	}
}

public class Step4_promise_exercise {
	public static void main(String[] args) {
		Vertx vertx = Vertx.vertx();
		vertx.deployVerticle(new VerticleWithPromise());
	}
}
