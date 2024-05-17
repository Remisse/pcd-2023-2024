package pcd.lab05.mandelbrot.version4_concurrent_extended;

public class StartSynch {

	private boolean started;
	private Task fullJob;
	
	public StartSynch(){
		started = false;
	}
	
	public synchronized Task waitStart() {
		while (!started) {
			try {
				wait();
			} catch (InterruptedException ex) {
				ex.printStackTrace();
			}
		}
		started = false;
		return fullJob;
	}

	public synchronized void notifyStarted(Complex c0, double diam) {
		started = true;
		fullJob = new Task(c0, diam);
		notifyAll();
	}
}
