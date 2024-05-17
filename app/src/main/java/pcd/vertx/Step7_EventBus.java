package pcd.vertx;

import io.vertx.core.*;
import io.vertx.core.eventbus.EventBus;

class MyAgent1 extends AbstractVerticle {
	
	 public void start(Promise<Void> startPromise) {
		log("started.");
		EventBus eb = this.getVertx().eventBus();
		eb.consumer("my-topic", message -> {
			log("new message: " + message.body());
		});		
		log("Ready.");
		startPromise.complete();
	}

	private void log(String msg) {
		System.out.println("[REACTIVE AGENT #1]["+Thread.currentThread()+"] " + msg);
	}
}

class MyAgent2 extends AbstractVerticle {
	
	public void start() {
		log("started.");
		EventBus eb = this.getVertx().eventBus();
		eb.publish("my-topic", "test");
	}

	private void log(String msg) {
		System.out.println("[REACTIVE AGENT #2]["+Thread.currentThread()+"] " + msg);
	}
}

public class Step7_EventBus {

	public static void main(String[] args) {
		Vertx  vertx = Vertx.vertx();
		vertx.deployVerticle(new MyAgent1(), res -> {
			/* deploy the second verticle only when the first has completed */
			vertx.deployVerticle(new MyAgent2());
		});
	}
}

