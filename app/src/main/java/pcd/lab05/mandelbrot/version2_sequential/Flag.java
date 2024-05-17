package pcd.lab05.mandelbrot.version2_sequential;

public class Flag {
	
	private boolean isSet;
	
	public Flag(){
		isSet = false;
	}
	
	public synchronized void set(){
		isSet = true;
	}
	
	public synchronized boolean isSet(){
		return isSet;
	}
	
	public synchronized void reset(){
		isSet = false;
	}

}
