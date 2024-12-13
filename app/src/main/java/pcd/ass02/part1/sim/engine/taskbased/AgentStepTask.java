package pcd.ass02.part1.sim.engine.taskbased;

import java.util.concurrent.Callable;

import pcd.ass01.sim.model.*;
import pcd.common.Flag;

public class AgentStepTask implements Callable<Void> {

	private AbstractAgent agent;
	private Flag stopFlag;
	private int dt;
	
	public AgentStepTask(AbstractAgent agent,  int dt, 	Flag stopFlag) {
		this.agent = agent;
		this.dt = dt;
		this.stopFlag = stopFlag;
	}
	
	public Void call() {
		if (!stopFlag.isSet()) {
			this.agent.step(dt);
		}
		return null;
	}
}
