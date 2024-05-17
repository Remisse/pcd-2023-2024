package pcd.lab05.jpf_examples;

import java.util.concurrent.locks.Lock;

public class MyWorkerA extends Worker {
	
	private Lock lock;
	private Counter count;
	
	public MyWorkerA(String name, Lock lock, Counter count){
		super(name);
		this.lock = lock;
		this.count = count;
	}
	
	public void run(){
		while (true){
		  action1();	
		  try {
			  lock.lockInterruptibly();
			  count.inc();
			  assert count.getValue() == 1;
			  action2();	
			  action3();	
		  } catch (InterruptedException ex) {
		  } finally {
			  count.dec();
			  lock.unlock();
		  }
		}
	}
	
	protected void action1(){
		println("a1");
		wasteRandomTime(100,500);	
	}
	
	protected void action2(){
		println("a2");
		wasteRandomTime(300,700);	
	}
	protected void action3(){
		println("a3");
		wasteRandomTime(300,700);	
	}
}

