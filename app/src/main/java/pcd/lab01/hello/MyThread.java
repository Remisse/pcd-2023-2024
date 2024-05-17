package pcd.lab01.hello;

public class MyThread extends Thread {

	public MyThread(String myName){
		super(myName);
	}
	
	public void run(){
		log("Hello concurrent world!");
		log("Sleeping for 5 secs...");
		try {
			Thread.sleep(5000);
		} catch (InterruptedException ex) {
			ex.printStackTrace();
		}
		log("Done.");
	}
	
	private void log(String msg) {
		System.out.println("[ " + this.getName()+ " ][ " + System.currentTimeMillis() + " ] " + msg); 
	}
}
