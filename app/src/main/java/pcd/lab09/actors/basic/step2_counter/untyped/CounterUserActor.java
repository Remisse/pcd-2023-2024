package pcd.lab09.actors.basic.step2_counter.untyped;

import akka.actor.*;

public class CounterUserActor extends AbstractActor {

	@Override
	public Receive createReceive() {
		return receiveBuilder()
				.match(StartMsg.class, this::onStartMsg)
				.match(CounterMsgProtocol.CounterValueMsg.class, this::onCounterValueMsg)
				.build();
		
	}

	private void onStartMsg(StartMsg msg) {
		msg.counter.tell(new CounterMsgProtocol.IncMsg(), this.getSelf());
		msg.counter.tell(new CounterMsgProtocol.IncMsg(), this.getSelf());
		msg.counter.tell(new CounterMsgProtocol.GetValueMsg(this.getContext().getSelf()), this.getSelf());
	}

	private void onCounterValueMsg(CounterMsgProtocol.CounterValueMsg msg){
		log("value: " + msg.value);
	}

	/* messages */

	static public class StartMsg implements CounterMsgProtocol.CounterUserMsg  {
		public final ActorRef counter;
		public StartMsg(ActorRef counter) {
			this.counter = counter;
		}
	}	
	
	private void log(String msg) {
		System.out.println("[CounterUserActor] " + msg);
	}
}
