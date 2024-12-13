package pcd.lab09.actors.basic.step2_counter.typed;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;

public class CounterActorBehaviour extends AbstractBehavior<CounterMsgProtocol.CounterMsg> {

	private int count;
	
	private CounterActorBehaviour(ActorContext<CounterMsgProtocol.CounterMsg> context) {
		super(context);
		count = 0;
	}

	@Override
	public Receive<CounterMsgProtocol.CounterMsg> createReceive() {
		return newReceiveBuilder()
				.onMessage(CounterMsgProtocol.IncMsg.class, this::onIncMsg)
				.onMessage(CounterMsgProtocol.GetValueMsg.class, this::onGetValueMsg)
				.build();
		
	}

	private Behavior<CounterMsgProtocol.CounterMsg> onIncMsg(CounterMsgProtocol.IncMsg msg) {
		this.getContext().getLog().info("inc");
		count++;
		return this;
	}

	private Behavior<CounterMsgProtocol.CounterMsg> onGetValueMsg(CounterMsgProtocol.GetValueMsg msg) {
		this.getContext().getLog().info("getValue");
		msg.replyTo.tell(new CounterMsgProtocol.CounterValueMsg(count));
		return this;
	}
	

	
	/* public factory to create the actor */

	public static Behavior<CounterMsgProtocol.CounterMsg> create() {
		return Behaviors.setup(CounterActorBehaviour::new);
	}
}
