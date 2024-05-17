package pcd.lab05.mandelbrot.version4_concurrent_extended;

import java.util.LinkedList;

public class TaskBag {

	private LinkedList<Task> buffer;

	public TaskBag() {
		buffer = new LinkedList<Task>();
	}

	public synchronized void clear() {
		buffer.clear();
	}
	
	public synchronized void addNewTask(Task task) {
		buffer.addLast(task);
		notifyAll();
	}

	public synchronized Task getATask() {
		while (buffer.isEmpty()) {
			try {
				wait();
			} catch (Exception ex) {}
		}
		return buffer.removeFirst(); 
	}
	
}
