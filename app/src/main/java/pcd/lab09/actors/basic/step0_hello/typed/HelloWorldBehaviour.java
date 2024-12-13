package pcd.lab09.actors.basic.step0_hello.typed;

import akka.actor.typed.*;
import akka.actor.typed.javadsl.*;

public class HelloWorldBehaviour extends AbstractBehavior<HelloWorldMsgProtocol.SayHello> {

	private int helloCounter;
	
	private HelloWorldBehaviour(ActorContext<HelloWorldMsgProtocol.SayHello> context) {
		super(context);
		helloCounter = 0;
	}

	/* configuring message handlers in this behavior */
	
	@Override
	public Receive<HelloWorldMsgProtocol.SayHello> createReceive() {
		return newReceiveBuilder().onMessage(HelloWorldMsgProtocol.SayHello.class, this::onSayHello).build();
	}

	/* message handler for SayHello */
	
	private Behavior<HelloWorldMsgProtocol.SayHello> onSayHello(HelloWorldMsgProtocol.SayHello msg) {
		helloCounter++;
		getContext().getLog().info("Hello " + msg.getContent() + " from " + this.getContext().getSelf() + " - count " + helloCounter);
		return this;
	}

	/* factory method */
	
	public static Behavior<HelloWorldMsgProtocol.SayHello> create() {
		return Behaviors.setup(HelloWorldBehaviour::new);
	}


}