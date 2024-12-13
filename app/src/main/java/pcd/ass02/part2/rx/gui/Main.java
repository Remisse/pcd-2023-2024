package pcd.ass02.part2.rx.gui;

import pcd.ass02.part2.common.View;

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
