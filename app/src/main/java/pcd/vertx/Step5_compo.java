package pcd.vertx;

import io.vertx.core.*;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.file.FileSystem;

class TestCompo extends AbstractVerticle {
	
	public void start() {
		FileSystem fs = vertx.fileSystem();    		

		
		Future<Buffer> f1 = fs.readFile("build.gradle.kts");
		Future<Buffer> f2 = fs.readFile("../settings.gradle.kts");
				
		Future
		.all(f1,f2)
		.onSuccess((CompositeFuture res) -> {
			log("COMPOSITE => \n"+res.result().list());			
		}); 
	}

	private void log(String msg) {
		System.out.println("[REACTIVE AGENT]["+Thread.currentThread()+"]" + msg);
	}
}

public class Step5_compo {

	public static void main(String[] args) {
		Vertx  vertx = Vertx.vertx();
		vertx.deployVerticle(new TestCompo());
	}
}

