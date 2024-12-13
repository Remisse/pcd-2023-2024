package pcd.lab07;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.file.FileSystem;

// Ogni verticle ha un proprio event loop separato. Dunque, due verticle
// diversi non possono causarsi problemi a vicenda. Possono comunicare solo
// attraverso messaggi (publish-subscribe) (Step7).

// Un verticle NON è equivalente a un task; è più simile a un thread che
// esegue una pool di task. Sarebbe sbagliato lanciare 10 verticle per leggere
// 10 file.
class MyReactiveAgent extends AbstractVerticle {
    // Le istruzioni all'interno di questo metodo vengono
    // eseguite in modo sempre deterministico.
    public void start() {
        log("1 - doing the async call...");

        FileSystem fs = getVertx().fileSystem();
        Future<Buffer> f1 = fs.readFile("app/build.gradle.kts");

        f1.onComplete((AsyncResult<Buffer> res) -> {
            log("4 - BUILD \n" + res.result().toString().substring(0, 160));
        });

        log("2 - doing the seconf async call...");

        fs.readFile("settings.gradle.kts").onComplete((AsyncResult<Buffer> res) -> {
            log("4 - SETTINGS \n" + res.result().toString().substring(0, 160));
        });

        log("3 - done");
    }

    private void log(String msg) {
        System.out.println(Thread.currentThread() + " " + msg);
    }
}

public class Step2_withverticle {
    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new MyReactiveAgent());
    }
}

