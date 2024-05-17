package pcd.lab05.jpf_examples;

import java.util.concurrent.locks.Lock;

public class MyWorkerB extends Worker {
	
	private Lock lock;
	private Counter count;
	
	public MyWorkerB(String name, Lock lock, Counter count){
		super(name);
		this.lock = lock;
		this.count = count;
	}

	public void run(){
		while (true){
		  try {
			  lock.lockInterruptibly();
			  count.inc();
			  assert count.getValue() == 1;
			  action1();	
			  action2();
		  } catch (InterruptedException ex) {
		  } finally {
			  count.dec();
			  lock.unlock();
		  }
		  action3();
		}
	}
	
	protected void action1(){
		println("b1");
		wasteRandomTime(0,1000);	
	}
	
	protected void action2(){
		println("b2");
		wasteRandomTime(100,200);	
	}
	protected void action3(){
		println("b3");
		wasteRandomTime(1000,2000);	
	}
}
