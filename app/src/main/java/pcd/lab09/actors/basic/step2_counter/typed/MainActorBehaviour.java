package pcd.lab09.actors.basic.step2_counter.typed;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import pcd.lab09.actors.basic.step2_counter.typed.CounterUserActor.StartMsg;
import pcd.lab09.actors.basic.step2_counter.typed.MainActorBehaviour.BootMsg;


public class MainActorBehaviour extends AbstractBehavior<BootMsg> {


	private MainActorBehaviour(ActorContext<BootMsg> context) {
		super(context);
	}

	@Override
	public Receive<BootMsg> createReceive() {
		return newReceiveBuilder()
				.onMessage(BootMsg.class, this::onBootMsg)
				.build();	
	}

	private Behavior<BootMsg> onBootMsg(BootMsg msg) {
		ActorRef<CounterMsgProtocol.CounterMsg> counter = this.getContext().spawn(CounterActorBehaviour.create(), "myCounter");
		ActorRef<CounterMsgProtocol.CounterUserMsg> counterUser = this.getContext().spawn(CounterUserActor.create(), "myCounterUser");
		counterUser.tell(new StartMsg(counter));
		return this;
	}

	public static Behavior<BootMsg> create() {
		return Behaviors.setup(MainActorBehaviour::new);
	}
	
	/* types of messages */
	
	static public final class BootMsg {}
}
