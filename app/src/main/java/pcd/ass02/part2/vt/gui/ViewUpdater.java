package pcd.ass02.part2.vt.gui;

import pcd.common.Flag;
import pcd.ass02.part2.common.Report;
import pcd.ass02.part2.common.View;
import pcd.ass02.part2.vt.lib.*;

public class ViewUpdater implements Runnable {
	
	private Flag stopFlag;
	private View view;
	private FutureWithSnapshots<Report> fut;
	
	public ViewUpdater(View view, FutureWithSnapshots<Report> fut, Flag stopFlag) {
		this.view = view;
		this.stopFlag = stopFlag;
		this.fut = fut;
	}
	
	public void run() {
		while (!stopFlag.isSet()) {
			Report rep = fut.getSnapshot();
			if (rep != null) {
				view.updateOutput(fut.getSnapshot());
			}
			try {
				Thread.sleep(100);
			} catch (Exception ex) {}
		}
	}

}
