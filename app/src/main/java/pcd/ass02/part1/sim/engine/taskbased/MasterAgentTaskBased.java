package pcd.ass02.part1.sim.engine.taskbased;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;

import pcd.ass01.sim.engine.AbstractSimulator;
import pcd.common.Flag;

public class MasterAgentTaskBased extends Thread {
	
	private ExecutorService executor;
	private boolean toBeInSyncWithWallTime;
	private int nStepsPerSec;
	private int numSteps;

	private long currentWallTime;
	
	private AbstractSimulator simulator;
	private Flag stopFlag;
	private Semaphore done;
	
	public MasterAgentTaskBased(AbstractSimulator sim, int poolSize, int numSteps, Flag stopFlag, Semaphore done, boolean syncWithTime) {
		toBeInSyncWithWallTime = false;
		this.simulator = sim;
		this.stopFlag = stopFlag;
		this.numSteps = numSteps;
		this.done = done;
		executor = Executors.newFixedThreadPool(poolSize);
	
		if (syncWithTime) {
			this.syncWithTime(25);
		}
	}

	public void run() {
		
		log("booted");
		
		var simEnv = simulator.getSimulationModel().getEnvironment();
		var simAgents = simulator.getSimulationModel().getAgents();
		
		simEnv.init();
		for (var a: simAgents) {
			a.init(simEnv);
		}

		int t = simulator.getInitialTime();
		int dt = simulator.getTimeStep();
		
		simulator.notifyReset(t, simAgents, simEnv);
					
		log("starting the simulation loop.");

		int step = 0;
		currentWallTime = System.currentTimeMillis();

		try {
			while (!stopFlag.isSet() &&  step < numSteps) {
				
				simEnv.step(dt);
				simEnv.cleanActions();

			    List<Future<Void>> futs = new LinkedList<Future<Void>>();

				for (var ag: simAgents) {
					if (!stopFlag.isSet()) {
						futs.add(executor.submit(new AgentStepTask(ag, dt, stopFlag)));
					} else {
						break;
					}
				}
				
				if (!stopFlag.isSet()) {
					
					for (var f: futs) {
						f.get();
					}
					
					/* executed actions */
					simEnv.processActions();
									
					simulator.notifyNewStep(t, simAgents, simEnv);
		
					if (toBeInSyncWithWallTime) {
						syncWithWallTime();
					}
					
					/* updating logic time */
					
					t += dt;
					step++;
				} 

			}	
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		log("done");
		stopFlag.set();

		done.release();
	}

	private void syncWithTime(int nStepsPerSec) {
		this.toBeInSyncWithWallTime = true;
		this.nStepsPerSec = nStepsPerSec;
	}

	private void syncWithWallTime() {
		try {
			long newWallTime = System.currentTimeMillis();
			long delay = 1000 / this.nStepsPerSec;
			long wallTimeDT = newWallTime - currentWallTime;
			currentWallTime = System.currentTimeMillis();
			if (wallTimeDT < delay) {
				Thread.sleep(delay - wallTimeDT);
			}
		} catch (Exception ex) {}
		
	}
	
	private void log(String msg) {
		System.out.println("[MASTER] " + msg);
	}
	
	
}
