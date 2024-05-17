package pcd.lab05.mandelbrot.version3_concurrent;

public interface InputListener {

	void started(Complex c0, double diam);
	
	void stopped();
}
