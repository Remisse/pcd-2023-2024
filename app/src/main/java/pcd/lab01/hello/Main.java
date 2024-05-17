package pcd.lab01.hello;

public class Main {

	public static void main(String[] args) throws Exception {
		
		log("Number of processors: " + Runtime.getRuntime().availableProcessors());
		
		MyThread myThread = new MyThread("MySimpleThread");
		myThread.start();		
		
		log("Thread spawned.");
		log("Waiting for its termination.");
		
		myThread.join();

		log("Completed.");
		
	}

	private static void log(String msg) {
		System.out.println("[ " + Thread.currentThread().getName()+ " ][ " + System.currentTimeMillis() + " ] " + msg); 
	}
	
}
