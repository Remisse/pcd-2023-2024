package pcd.ass_single.part1.event;

import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;

public class LocalEventBus {
    private static volatile EventBus instance;

    private LocalEventBus() {
    }

    public static EventBus get() {
        synchronized (LocalEventBus.class) {
            if (instance == null) {
                instance = Vertx.vertx().eventBus();
            }
            return instance;
        }
    }
}
