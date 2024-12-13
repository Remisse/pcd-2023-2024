package pcd.ass02.part2.common;

public class View {

	private GUIFrame gui;

	public View(String word, String address, int depth){	
		gui = new GUIFrame(word, address, depth);
	}
	
	public void setController(ViewListener contr) {
		gui.setController(contr);
	}

	public  void display() {
		gui.display();
    }
	
	public void reset() {
		gui.reset();
	}
	
	public void updateOutput(String address, int occ) {
		gui.appendOutput("Page: " + address + " - occurrences: " + occ + "\n"); 
	}

	public void updateOutput(Report report) {
		gui.updateOutput(report.toString());
	}
}
