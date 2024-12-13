package pcd.ass02.part2.event.gui;

import io.vertx.core.Future;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;
import pcd.ass02.part2.common.Report;
import pcd.ass02.part2.common.View;
import pcd.ass02.part2.common.ViewListener;
import pcd.ass02.part2.event.lib.MyEventBus;
import pcd.ass02.part2.event.lib.WordCountLib;
import pcd.common.Flag;

public class Controller implements ViewListener {

	private Flag stopFlag;
	private View gui;

	public Controller() {
		this.stopFlag = new Flag();
	}

	public void attach(View gui) {
		this.gui = gui;
		gui.setController(this);

		EventBus eb = MyEventBus.getInstance(); // this.getVertx().eventBus();
		eb.consumer("page-info", message -> {
			if (!stopFlag.isSet()) {
				JsonObject obj = (JsonObject) message.body();
				gui.updateOutput(obj.getString("page"), obj.getInteger("numOcc"));
			}
		});
	}

	public void notifyStarted(String word, String address, int depth) {
		stopFlag.reset();
		Future<Report> fut = WordCountLib.getWordOccurrencesDyn(address, word, depth, stopFlag);
		fut.onSuccess((report) -> {
			if (!stopFlag.isSet()) {
				log("Completed \n " + report.toString());
				gui.updateOutput(report);
				gui.reset();
			}				
		}).onFailure((m) -> {
			log("Stopped");
			gui.reset();	
		});

	}

	public void notifyStopped() {
		stopFlag.set();
	}

	private void log(String msg){
		System.out.println("[CONTROLLER] " + msg);
	}
	
}
