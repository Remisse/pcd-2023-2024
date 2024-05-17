package pcd.vertx;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;

class TestExecBlocking2 extends AbstractVerticle {

	// private int x = 0;
	
	public void start() {
		log("before");

		// x++;
		
		Promise<Integer> p = Promise.promise();
		
		vertx.getOrCreateContext().runOnContext( (v) -> {
			// Call some blocking API that takes a significant amount of time to return
			log("blocking computation started ");
			try {
				// x++;
				Thread.sleep(5000);
				p.complete(100);
				
				/* notify promise completion */
			} catch (Exception ex) {
			}
		});

		log("after triggering a blocking computation...");
		// x++;

		p.future().onComplete((fut) -> {
			log("result: " + fut );
		});
	}

	private void log(String msg) {
		System.out.println("[REACTIVE AGENT] ["+Thread.currentThread()+"] " + msg);
	}
}

public class Step6_withblocking2 {

	public static void main(String[] args) {
		Vertx vertx = Vertx.vertx();
		vertx.deployVerticle(new TestExecBlocking2());
	}
}
