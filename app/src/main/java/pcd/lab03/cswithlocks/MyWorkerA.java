package pcd.lab03.cswithlocks;

import java.util.concurrent.locks.Lock;

public class MyWorkerA extends Worker {
	
	private Lock lock;
	
	public MyWorkerA(String name, Lock lock){
		super(name);
		this.lock = lock;
	}
	
	public void run(){
		while (true){
		  action1();	
		  try {
			  lock.lockInterruptibly();
			  action2();	
			  action3();	
		  } catch (InterruptedException ex) {
		  } finally {
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

