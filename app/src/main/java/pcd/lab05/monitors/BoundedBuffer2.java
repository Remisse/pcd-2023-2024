package pcd.lab05.monitors;

import java.util.LinkedList;
import java.util.concurrent.locks.*;

public class BoundedBuffer2<Item> implements IBoundedBuffer<Item> {

	private LinkedList<Item> buffer;
	private int maxSize;
	private Lock mutex;
	private Condition notEmpty, notFull;

	public BoundedBuffer2(int size) {
		buffer = new LinkedList<Item>();
		maxSize = size;
		mutex = new ReentrantLock();
		notEmpty = mutex.newCondition();
		notFull = mutex.newCondition();
	}

	public void put(Item item) throws InterruptedException {
		try {
			mutex.lock();
			if (isFull()) {
				notFull.await();
			}
			buffer.addLast(item);
			notEmpty.signal();
		} finally {
			mutex.unlock();
		}
	}

	public Item get() throws InterruptedException {
		try {
			mutex.lock();
			if (isEmpty()) {
				notEmpty.await();
			}
			Item item = buffer.removeFirst();
			notFull.signal();
			return item;
		} finally {
			mutex.unlock();
		}
	}

	private boolean isFull() {
		return buffer.size() == maxSize;
	}

	private boolean isEmpty() {
		return buffer.size() == 0;
	}
}
