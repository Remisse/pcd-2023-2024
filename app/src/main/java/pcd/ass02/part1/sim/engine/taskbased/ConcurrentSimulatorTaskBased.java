package pcd.ass02.part1.sim.engine.taskbased;

import java.util.concurrent.Semaphore;

import pcd.ass01.sim.engine.*;
import pcd.common.Flag;

public class ConcurrentSimulatorTaskBased extends AbstractSimulator {

	private int nWorkers;
	private Flag stopFlag;
	
	public ConcurrentSimulatorTaskBased(int nWorkers, Flag stopFlag) {
		super();
		this.nWorkers = nWorkers;
		this.stopFlag = stopFlag;
	}
	
	public void run(int nSteps, boolean syncWithTime) {
		startWallTime = System.currentTimeMillis();
		
		Semaphore done = new Semaphore(0);
		MasterAgentTaskBased agent = new MasterAgentTaskBased(this, nWorkers, nSteps, stopFlag, done, syncWithTime);
		agent.start();
		
		try {
			done.acquire();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		endWallTime = System.currentTimeMillis();		
	}
}
