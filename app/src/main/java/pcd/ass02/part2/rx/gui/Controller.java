package pcd.ass02.part2.rx.gui;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import io.reactivex.rxjava3.schedulers.Schedulers;
import io.reactivex.rxjava3.subjects.PublishSubject;
import pcd.ass02.part2.common.PageInfo;
import pcd.ass02.part2.common.Report;
import pcd.ass02.part2.common.View;
import pcd.ass02.part2.common.ViewListener;
import pcd.ass02.part2.rx.lib.WordCountLib;
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
	}

	public void notifyStarted(String word, String address, int depth) {
		stopFlag.reset();
		CompletableFuture.runAsync(() -> {
			var flow = WordCountLib.getWordOccurrencesDyn(address, word, depth, Optional.of(stopFlag));
			flow
			.subscribe(reportSnapshot -> {
				gui.updateOutput(reportSnapshot);
			}, (e) -> {
				log("Error " + e);
			}, () -> {
				log("Completed ");
				gui.reset();
			});
		});
	}

	public void notifyStopped() {
		stopFlag.set();
	}
	
	private void log(String msg){
		System.out.println("[CONTROLLER] " + msg);
	}

}
