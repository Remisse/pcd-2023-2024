package pcd.lab05.jpf_examples;

public class Counter {

	private int count;

	public Counter(int count){
		this.count = count;
	}

	public synchronized void inc(){
		count++;
	}

	public synchronized void dec(){
		count--;
	}
	
	public synchronized int getValue(){
		return count;
	}
}