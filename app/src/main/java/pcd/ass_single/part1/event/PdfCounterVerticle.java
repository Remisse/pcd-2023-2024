package pcd.ass_single.part1.event;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;
import pcd.ass_single.part1.common.Directory;
import pcd.ass_single.part1.common.Logger;
import pcd.ass_single.part1.common.Parsing;
import pcd.ass_single.part1.common.flag.AtomicFlag;
import pcd.ass_single.part1.common.flag.SuspendableFlag;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;

public class PdfCounterVerticle extends AbstractVerticle {
    private static final EventBus BUS = LocalEventBus.get();
    private static final Logger LOGGER = Logger.get();
    private final Directory startingDirectory;
    private final Pattern regex;
    private final AtomicFlag stopFlag;
    private final SuspendableFlag suspendFlag;

    private int total;
    private int parsed;
    private int found;

    public PdfCounterVerticle(final Directory startingDirectory, final Pattern regex, final AtomicFlag stopFlag,
                              final SuspendableFlag suspendFlag) {
        this.startingDirectory = startingDirectory;
        this.regex = regex;
        this.stopFlag = stopFlag;
        this.suspendFlag = suspendFlag;
    }

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        super.start();
        total = 0;
        parsed = 0;
        found = 0;
        BUS.publish("started", null);
        scan(startingDirectory)
                .onSuccess(s -> startPromise.complete())
                .onFailure(startPromise::fail);
    }

    private Future<Void> scan(final Directory directory) {
        suspendFlag.checkIn();
        if (stopFlag.isSet()) {
            return Future.failedFuture("stopped");
        }

        final var nestedDirFutures = asyncGetNestedDirectories(directory)
                .compose(dirs -> Future.all(dirs.stream().map(this::scan).toList()));

        final var pdfSearchFutures = asyncGetPdfs(directory);
        final Future<CompositeFuture> pdfParsingFutures = pdfSearchFutures.compose(pdfs -> {
            total += pdfs.size();
            final List<Future<Boolean>> futures = pdfs.stream()
                    .map(p -> asyncLoadPdf(p)
                            .compose(this::asyncDoesPdfMatch))
                    .peek(ignored -> parsed++)
                    .toList();
            return Future.all(futures);
        });
        pdfParsingFutures
                .map(CompositeFuture::list)
                .map(res -> res.stream()
                        .filter(r -> (boolean) r)
                        .count())
                .onSuccess(res -> {
                    found += res;
                    final JsonObject result = new JsonObject();
                    result.put("total", total);
                    result.put("parsed", parsed);
                    result.put("found", found);
                    BUS.publish("result", result);
                });
        return Future.all(nestedDirFutures, pdfParsingFutures).mapEmpty();
    }

    private Future<List<Directory>> asyncGetNestedDirectories(final Directory directory) {
        return getVertx().executeBlocking(directory::nestedDirectories);
    }

    private Future<List<File>> asyncGetPdfs(final Directory directory) {
        return getVertx().executeBlocking(() -> directory.filesOfType("pdf"));
    }

    private Future<Parsing.PDFWrapper> asyncLoadPdf(final File file) {
        return getVertx().executeBlocking(() -> {
            try {
                return Parsing.loadPdf(file);
            } catch (IOException e) {
                debugLog(e.toString());
                return Parsing.nilWrapper();
            }
        });
    }

    private Future<Boolean> asyncDoesPdfMatch(final Parsing.PDFWrapper pdf) {
        return getVertx().executeBlocking(() -> {
            pdf.error().ifPresent(this::debugLog);
            if (pdf.isNil() || pdf.isError()) {
                return false;
            }
            try {
                return Parsing.doesPdfSatisfyRegex(pdf.document().orElseThrow(), regex);
            } catch (IOException e) {
                debugLog(e.toString());
                return false;
            }
        });
    }

    private void debugLog(final String msg) {
        LOGGER.debugLog(PdfCounterVerticle.class.getSimpleName(), msg);
    }
}
