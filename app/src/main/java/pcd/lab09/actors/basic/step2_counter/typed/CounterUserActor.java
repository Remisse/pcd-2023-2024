package pcd.lab09.actors.basic.step2_counter.typed;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;

public class CounterUserActor extends AbstractBehavior<CounterMsgProtocol.CounterUserMsg> {

	private CounterUserActor(ActorContext<CounterMsgProtocol.CounterUserMsg> context) {
		super(context);
	}

	@Override
	public Receive<CounterMsgProtocol.CounterUserMsg> createReceive() {
		return newReceiveBuilder()
				.onMessage(StartMsg.class, this::onStartMsg)
				.onMessage(CounterMsgProtocol.CounterValueMsg.class, this::onCounterValueMsg)
				.build();
		
	}

	private Behavior<CounterMsgProtocol.CounterUserMsg> onStartMsg(StartMsg msg) {
		this.getContext().getLog().info("started");
		msg.counter.tell(new CounterMsgProtocol.IncMsg());
		msg.counter.tell(new CounterMsgProtocol.IncMsg());
		msg.counter.tell(new CounterMsgProtocol.GetValueMsg(this.getContext().getSelf()));
		return this;
	}

	private Behavior<CounterMsgProtocol.CounterUserMsg> onCounterValueMsg(CounterMsgProtocol.CounterValueMsg msg){
		this.getContext().getLog().info("value: " + msg.value);
		return this;
	}

	/* messages */

	static public class StartMsg implements CounterMsgProtocol.CounterUserMsg  {
		public final ActorRef<CounterMsgProtocol.CounterMsg> counter;
		public StartMsg(ActorRef<CounterMsgProtocol.CounterMsg> counter) {
			this.counter = counter;
		}
	}	
	
	/* public factory to create the actor */

	public static Behavior<CounterMsgProtocol.CounterUserMsg> create() {
		return Behaviors.setup(CounterUserActor::new);
	}
}
