package pcd.ass_single.part1.event.verticle;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;
import pcd.ass_single.part1.common.Directory;
import pcd.ass_single.part1.common.flag.AtomicFlag;
import pcd.ass_single.part1.common.flag.SuspendableFlag;
import pcd.ass_single.part1.event.LocalEventBus;

import java.io.File;
import java.util.List;

public class ScannerVerticle extends AbstractVerticle {
    private static final EventBus BUS = LocalEventBus.get();
    private final Directory startingDirectory;
    private final AtomicFlag stopFlag;
    private final SuspendableFlag suspendFlag;
    private boolean firstTime = true;
    private int resultsSent;

    public ScannerVerticle(final Directory startingDirectory, final AtomicFlag stopFlag, final SuspendableFlag suspendFlag) {
        this.startingDirectory = startingDirectory;
        this.stopFlag = stopFlag;
        this.suspendFlag = suspendFlag;
    }

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        if (firstTime) {
            firstTime = false;
            BUS.publish("started", null);
        }
        scan(startingDirectory)
                .onComplete(f -> {
                    BUS.publish("scan-over", new JsonObject().put("resultsSent", resultsSent));
                    startPromise.complete();
                });
    }

    private Future<Void> scan(final Directory directory) {
        suspendFlag.checkIn();
        if (stopFlag.isSet()) {
            return Future.succeededFuture(null);
        }
        var pdfFutures = asyncGetPdfs(directory)
                .map(l -> l.stream()
                        .map(File::getPath)
                        .toList())
                .onSuccess(l -> {
                    if (!l.isEmpty()) {
                        BUS.publish("to-parse", new JsonObject().put("pdfs", l));
                        resultsSent++;
                    }
                });
        var nestedDirFutures = asyncGetNestedDirectories(directory)
                .compose(dirs -> Future.all(dirs.stream().map(this::scan).toList()));
        return Future.all(pdfFutures, nestedDirFutures).mapEmpty();
    }

    private Future<List<Directory>> asyncGetNestedDirectories(final Directory directory) {
        return getVertx().executeBlocking(directory::nestedDirectories);
    }

    private Future<List<File>> asyncGetPdfs(final Directory directory) {
        return getVertx().executeBlocking(() -> directory.filesOfType("pdf"));
    }
}
