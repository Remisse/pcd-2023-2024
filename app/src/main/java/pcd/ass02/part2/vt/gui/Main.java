package pcd.ass02.part2.vt.gui;

import java.util.concurrent.Future;

import pcd.ass02.part2.common.Report;
import pcd.ass02.part2.common.View;
import pcd.ass02.part2.vt.lib.WordCountLib;

public class Main {

	public static void main(String[] args) throws Exception {
		
		String word = "paese";
		String address = "https://it.wikipedia.org/wiki/Italia";
		int depth = 2;
		
		Controller controller = new Controller();
		View view = new View(word, address, depth);
		controller.attach(view);
		view.display();
	}
}
