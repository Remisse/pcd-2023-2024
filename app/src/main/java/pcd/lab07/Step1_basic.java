package pcd.lab07;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.file.FileSystem;

public class Step1_basic {
    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        FileSystem fs = vertx.fileSystem();

        log("doing the async call... ");
        // "Future" di Vertx, non di Java. Equivalente alle Promise di JS.
        Future<Buffer> fut = fs.readFile("app/build.gradle.kts");

        // Equivalente a "then".
        // L'ordine dei messaggi "BUILD" e "async call done" è non deterministico.
        fut.onComplete((AsyncResult<Buffer> res) -> {
            log("BUILD \n" +
                    (res.failed() ? res.cause().toString() : res.result().toString().substring(0, 160)));
        });
        log("async call done. Waiting some time... ");

        try {
            Thread.sleep(1000);
        } catch (Exception ex) {
        }
        log("done");
    }

    private static void log(String msg) {
        System.out.println(Thread.currentThread() + " " + msg);
    }
}

