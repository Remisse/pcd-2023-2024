package pcd.lab03.liveness.deadlock_simplest;

import pcd.lab03.liveness.accounts_exercise.BaseAgent;

public class ThreadA extends BaseAgent {
 
	private Resource res;
	
	public ThreadA(Resource res){
		this.res = res;
	}
	
	public void run(){
		while (true){
			waitAbit();
			res.rightLeft();
		}
	}	
}
