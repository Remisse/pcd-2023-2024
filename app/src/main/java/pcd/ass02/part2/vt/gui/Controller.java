package pcd.ass02.part2.vt.gui;

import pcd.ass02.part2.common.*;
import pcd.ass02.part2.vt.lib.FutureWithSnapshots;
import pcd.ass02.part2.vt.lib.WordCountLib;
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
		new Thread(() -> {
			
			FutureWithSnapshots<Report> fut = WordCountLib.getWordOccurrences(address, word, depth);

			Thread
				.ofVirtual()
				.name("view-updated")
				.start(new ViewUpdater(gui, fut, stopFlag));
			
			try {
				Report report = fut.get();
				
				log("Completed \n " + report.toString());
				gui.updateOutput(report);
				gui.reset();
				
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}).start();
	}
	
	public void notifyStopped() {
		stopFlag.set();
	}

	private void log(String msg){
		System.out.println("[CONTROLLER] " + msg);
	}
}
