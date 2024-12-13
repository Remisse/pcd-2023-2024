package pcd.ass02.part2.common;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;

public class GUIFrame extends JFrame implements ActionListener {

	private JButton start;
	private JButton stop;
	private JTextField word;
	private JTextField address;
	private JTextField depth;
	private JTextField status;
	private JTextArea output;
	
	private ViewListener controller;
	
	public GUIFrame(String defWord, String defAddress, int defDepth){
		setTitle("Search Occurrences");
		setSize(600,660);		
		word = new JTextField(5);
		word.setText(defWord);

		address = new JTextField(20);
		address.setText(defAddress);
		
		depth = new JTextField(2);
		depth.setText("" + defDepth);

		status = new JTextField(5);
		status.setText("Idle");
		
		output = new JTextArea(30,10);
		output.setText("");
		output.setEditable(false);
		JScrollPane scroller = new JScrollPane(output);
		JScrollBar bar = new JScrollBar();
		scroller.add(bar);

		start = new JButton("start");
		stop  = new JButton("stop");
		stop.setEnabled(false);
		
		Container cp = getContentPane();
		JPanel panel = new JPanel();
		
		Box p0 = new Box(BoxLayout.X_AXIS);
		p0.add(new JLabel("Word: "));
		p0.add(word);
		p0.add(Box.createHorizontalStrut(20));
		p0.add(new JLabel("Address: "));
		p0.add(address);
		p0.add(Box.createHorizontalStrut(20));
		p0.add(new JLabel("Depth: "));
		p0.add(depth);
		
		Box p1 = new Box(BoxLayout.X_AXIS);
		p1.add(start);
		p1.add(stop);

		Box p3 = new Box(BoxLayout.X_AXIS);
		p3.add(scroller);

		Box p4 = new Box(BoxLayout.X_AXIS);
		p4.add(new JLabel("Status: "));
		p4.add(status);
		
		
		Box p2 = new Box(BoxLayout.Y_AXIS);
		p2.add(p0);
		p2.add(Box.createVerticalStrut(10));
		p2.add(p1);
		p2.add(Box.createVerticalStrut(10));
		p2.add(p3);
		p2.add(Box.createVerticalStrut(10));
		p2.add(p4);
		
		panel.add(p2);
		cp.add(panel);
		
		addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent ev){
				System.exit(-1);
			}
			public void windowClosed(WindowEvent ev){
				System.exit(-1);
			}
		});

		start.addActionListener(this);
		stop.addActionListener(this);
	}
	
	public void setController(ViewListener contr) {
		this.controller = contr;
	}
	
	public void updateOutput(String report) {
		try {
			SwingUtilities.invokeAndWait(() -> {
				output.setText(report);
			});
		} catch (Exception ex) {}
	}

	public void appendOutput(String line) {
		SwingUtilities.invokeLater(() -> {
			output.append(line);
		});
	}

	public void updateOutput(String page, int occ) {
		try {
			SwingUtilities.invokeAndWait(() -> {
				output.append("Trovate " + occ + " occorrenze in " + page + "\n");
				System.out.println("done");
			});
		} catch (Exception ex) {}
	}

	
	public void actionPerformed(ActionEvent ev){
		Object src = ev.getSource();
		if (src==start){	
			try {
				String w = word.getText();
				String addr = address.getText();
				int d = Integer.parseInt(depth.getText());				
				start.setEnabled(false);
				stop.setEnabled(true);
				output.setText("");
				status.setText("Working");
				controller.notifyStarted(w, addr, d);
			} catch (Exception ex) {}
		} else if (src == stop){
			start.setEnabled(true);
			stop.setEnabled(false);
			controller.notifyStopped();
			status.setText("Idle");
		}
		
	}
	
	public void reset() {
		SwingUtilities.invokeLater(()-> {
			start.setEnabled(true);
			stop.setEnabled(false);
			status.setText("Idle");
		});
	}


	public void display() {
        javax.swing.SwingUtilities.invokeLater(() -> {
        	this.setVisible(true);
        });
    }
	
	
}
