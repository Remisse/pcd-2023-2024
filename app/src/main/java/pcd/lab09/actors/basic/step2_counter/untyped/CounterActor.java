package pcd.lab09.actors.basic.step2_counter.untyped;

import akka.actor.*;

public class CounterActor extends AbstractActor {

	private int count;
	
	private CounterActor() {
		count = 0;
	}

	public Receive createReceive() {
		return receiveBuilder()
				.match(CounterMsgProtocol.IncMsg.class, this::onIncMsg)
				.match(CounterMsgProtocol.GetValueMsg.class, this::onGetValueMsg)
	            .build();
	}

	private void onIncMsg(CounterMsgProtocol.IncMsg msg) {
		count++;
	}

	private void onGetValueMsg(CounterMsgProtocol.GetValueMsg msg) {
		msg.replyTo.tell(new CounterMsgProtocol.CounterValueMsg(count), this.getSelf());
	}
	
}
