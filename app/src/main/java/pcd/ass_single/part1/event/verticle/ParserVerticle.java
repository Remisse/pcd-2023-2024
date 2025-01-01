package pcd.ass_single.part1.event.verticle;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import pcd.ass_single.part1.common.Logger;
import pcd.ass_single.part1.common.Parsing;
import pcd.ass_single.part1.common.flag.AtomicFlag;
import pcd.ass_single.part1.common.flag.SuspendableFlag;
import pcd.ass_single.part1.event.LocalEventBus;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;

public class ParserVerticle extends AbstractVerticle {
    private static final EventBus BUS = LocalEventBus.get();
    private static final Logger LOGGER = Logger.get();
    private final Pattern regex;
    private final AtomicFlag stopFlag;
    private final SuspendableFlag suspendFlag;
    private MessageConsumer<Object> scanOverConsumer;
    private MessageConsumer<Object> toParseConsumer;
    private int resultsSentByScanner = -1;
    private int resultsConsumed;
    private int total = 0;
    private int parsed = 0;
    private int found = 0;

    public ParserVerticle(final Pattern regex, final AtomicFlag stopFlag, final SuspendableFlag suspendFlag) {
        this.regex = regex;
        this.stopFlag = stopFlag;
        this.suspendFlag = suspendFlag;
    }

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        scanOverConsumer = BUS.consumer("scan-over", m ->
                resultsSentByScanner = ((JsonObject) m.body()).getInteger("resultsSent"));
        toParseConsumer = BUS.consumer("to-parse", m -> {
            if (stopFlag.isSet()) {
                startPromise.tryComplete();
                return;
            }
            final JsonArray pdfPaths = ((JsonObject) m.body()).getJsonArray("pdfs");
            Future.all(pdfPaths.stream()
                            .map(p -> asyncLoadPdf((String) p).compose(this::asyncDoesPdfMatch))
                            .toList())
                    .map(CompositeFuture::list)
                    .map(this::asyncCount)
                    .onSuccess(res -> getVertx().executeBlocking(() -> {
                        BUS.publish("result", createResult());
                        resultsConsumed++;
                        if (resultsConsumed == resultsSentByScanner) {
                            startPromise.complete();
                        }
                        return null;
                    }));
        });
    }

    @Override
    public void stop() throws Exception {
        BUS.publish("result", createResult());
        scanOverConsumer.unregister();
        toParseConsumer.unregister();
    }

    private Future<Parsing.PDFWrapper> asyncLoadPdf(final String path) {
        return getVertx().executeBlocking(() -> {
            suspendFlag.checkIn();
            if (stopFlag.isSet()) {
                return Parsing.nilWrapper();
            }
            try {
                total++;
                return Parsing.loadPdf(new File(path));
            } catch (IOException e) {
                debugLog(e.toString());
                return Parsing.nilWrapper();
            }
        });
    }

    private Future<Boolean> asyncDoesPdfMatch(final Parsing.PDFWrapper pdf) {
        return getVertx().executeBlocking(() -> {
            suspendFlag.checkIn();
            pdf.error().ifPresent(this::debugLog);
            if (pdf.isNil() || pdf.isError() || stopFlag.isSet()) {
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

    private Future<Long> asyncCount(final List<?> list) {
        return getVertx().executeBlocking(() -> {
            suspendFlag.checkIn();
            if (stopFlag.isSet()) {
                return 0L;
            }
            var count = list.stream()
                    .peek(ignored -> parsed++)
                    .filter(r -> (boolean) r)
                    .count();
            found += (int) count;
            return count;
        });
    }

    private JsonObject createResult() {
        return new JsonObject()
                .put("total", total)
                .put("parsed", parsed)
                .put("found", found);
    }

    private void debugLog(final String msg) {
        LOGGER.debugLog(Thread.currentThread().getName(), msg);
    }
}
