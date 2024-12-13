package pcd.lab07;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Vertx;

class TestExecBlocking extends AbstractVerticle {

	// private int x = 0;
	
	public void start() {
		log("before");

		// x++;
	
		Future<Integer> res = this.getVertx().executeBlocking(() -> {
			// Call some blocking API that takes a significant amount of time to return
			log("blocking computation started");
			try {
				// x++;
				Thread.sleep(5000);
				
				/* notify promise completion */
				return 100;
			} catch (Exception ex) {
				
				/* notify failure */
				throw new Exception("exception");
			}
		});

		log("after triggering a blocking computation...");
		// x++;

		res.onComplete((AsyncResult<Integer> r) -> {
			log("result: " + r.result());
		});
	}

	private void log(String msg) {
		System.out.println("[REACTIVE AGENT] ["+Thread.currentThread()+"] " + msg);
	}
}

public class Step6_withblocking {

	public static void main(String[] args) {
		Vertx vertx = Vertx.vertx();
		vertx.deployVerticle(new TestExecBlocking());
	}
}
