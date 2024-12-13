package pcd.lab09.actors.basic.step0_hello.untyped;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.ActorSystem;

public class RunActorSystem {

	public static void main(String[] args) {
		
		final ActorSystem system = ActorSystem.create("my-actor-system");
		
		final ActorRef helloActor =  system.actorOf(Props.create(HelloWorldActor.class), "my-actor");
		helloActor.tell(new HelloWorldMsgProtocol.SayHello("World"), null);
		helloActor.tell(new HelloWorldMsgProtocol.SayHello("World Again"), null);
	}

}
