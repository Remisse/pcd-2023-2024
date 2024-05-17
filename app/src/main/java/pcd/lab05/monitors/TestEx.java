package pcd.lab05.monitors;

public class TestEx {

	public static void main(String[] args)  {

		Object lock = new Object();
        
		try {
			synchronized (lock){
		        lock.wait();
			}
		} catch (Exception ex){
			ex.printStackTrace();
		}
	}

}
