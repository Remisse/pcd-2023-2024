package pcd.lab05.jpf_examples;

import java.util.concurrent.locks.*;

public class TestCS {
	public static void main(String[] args) {
		Lock lock = new ReentrantLock();
		Counter counter = new Counter(0);
		new MyWorkerB("MyAgent-01", lock, counter).start();
		new MyWorkerA("MyAgent-02", lock, counter).start();		
	}
}
