package pcd.lab03.cswithlocks;

import java.util.concurrent.locks.Lock;

public class MyWorkerB extends Worker {
	
	private Lock lock;
	
	public MyWorkerB(String name, Lock lock){
		super(name);
		this.lock = lock;
	}

	public void run(){
		while (true){
		  try {
			  lock.lockInterruptibly();
			  action1();	
			  action2();
		  } catch (InterruptedException ex) {
		  } finally {
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
