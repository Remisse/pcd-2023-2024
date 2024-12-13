package pcd.lab09.actors.basic.step1_pingpong.typed;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;


public class BootActor extends AbstractBehavior<pcd.lab09.actors.basic.step1_pingpong.typed.BootActor.BootMsg> {

	private BootActor(ActorContext<BootMsg> context) {
		super(context);
	}

	@Override
	public Receive<BootMsg> createReceive() {
		return newReceiveBuilder()
				.onMessage(BootMsg.class, this::onBootMsg)
				.build();		
	}

	private Behavior<BootMsg> onBootMsg(BootMsg msg) {
		ActorRef<PingerPongerProtocol> pinger = this.getContext().spawn(PingerActor.create(), "pinger");
		ActorRef<PingerPongerProtocol> ponger = this.getContext().spawn(PongerActor.create(), "ponger");
		pinger.tell(new pcd.lab09.actors.basic.step1_pingpong.typed.PingerPongerProtocol.BootMsg(ponger));
		return this;
	}

	/* messages */
	
	static public class BootMsg {}

	/* public factory to create the actor */

	public static Behavior<BootMsg> create() {
		return Behaviors.setup(BootActor::new);
	}
}
