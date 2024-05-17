package pcd.lab02.check_act;

public class WorkerA extends Thread{
	
	private final BoundedCounter counter;
	private int ntimes;

	public WorkerA(BoundedCounter c, int ntimes){
		counter = c;
		this.ntimes = ntimes;
	}
	
	public void run(){
		try {
			for (int i = 0; i < ntimes; i++){
				synchronized (counter) {
					if (counter.getValue() > 0){
						counter.dec();
					}
				}
			}
		} catch (Exception ex){
			ex.printStackTrace();
		}
	}
}
