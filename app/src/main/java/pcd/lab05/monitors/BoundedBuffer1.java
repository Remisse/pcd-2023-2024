package pcd.lab05.monitors;

import java.util.LinkedList;

public class BoundedBuffer1<Item> implements IBoundedBuffer<Item> {

	private LinkedList<Item> buffer;
	private int maxSize;

	public BoundedBuffer1(int size) {
		buffer = new LinkedList<Item>();
		maxSize = size;
	}

	public synchronized void put(Item item) throws InterruptedException {
		while (isFull()) {
			wait();
		}
		buffer.addLast(item);
		notifyAll();
	}

	public synchronized Item get() throws InterruptedException {
		while (isEmpty()) {
			wait();
		}
		Item item = buffer.removeFirst();
		notifyAll();
		return item;
	}

	private boolean isFull() {
		return buffer.size() == maxSize;
	}

	private boolean isEmpty() {
		return buffer.size() == 0;
	}
}
