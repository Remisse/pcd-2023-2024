package pcd.ass02.part2.event.lib;

import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;

public class MyEventBus {

	static private EventBus instance;

	static public EventBus getInstance() {
		synchronized (MyEventBus.class) {
			if (instance == null) {
				instance = Vertx.vertx().eventBus();
			}
			return instance;
		}
	}
}
