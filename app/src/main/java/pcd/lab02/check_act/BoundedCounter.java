package pcd.lab02.check_act;

public class BoundedCounter {

	private int cont;
	private int min, max;
	
	public BoundedCounter(int min, int max){
		this.cont = this.min = min;
		this.max = max;
	}
	
	public void inc() throws OverflowException {
		synchronized (this){
			if (getValue() + 1 > max){
				throw new OverflowException();
			}
			cont++;
		}
	}

	public  void dec() throws UnderflowException {
		synchronized (this){
			if (getValue() - 1 < min){
				throw new UnderflowException();
			}
			cont--;
		}
	}
	
	public  int getValue(){
		synchronized (this){
			return cont;
		}
	}
}
