package pcd.lab05.mandelbrot.version4_concurrent_extended;

public interface InputListener {

	void started(Complex c0, double diam);
	
	void stopped();
}
