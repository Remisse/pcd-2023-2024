package pcd.lab09.actors.basic.step2_counter.untyped;

import akka.actor.*;

public class MainActor extends AbstractActor {

	@Override
	public Receive createReceive() {
		return receiveBuilder()
				.match(BootMsg.class, this::onBootMsg)
				.build();	
	}

	private void onBootMsg(BootMsg msg) {
		ActorRef counter = this.getContext().actorOf(Props.create(CounterActor.class), "myCounter");
		ActorRef counterUser = this.getContext().actorOf(Props.create(CounterUserActor.class), "myCounterUser");
		counterUser.tell(new CounterUserActor.StartMsg(counter), this.getSelf());
	}

	/* types of messages */
	
	static public final class BootMsg {}
}
