package pcd.lab09.actors.basic.step0_hello.typed;

import akka.actor.typed.ActorSystem;

public class HelloWorldRun {

	public static void main(String[] args) {
		
		final ActorSystem<HelloWorldMsgProtocol.SayHello> helloWorldActor = ActorSystem.create(HelloWorldBehaviour.create(),
				"hello-actor");

		helloWorldActor.tell(new HelloWorldMsgProtocol.SayHello("World"));
		helloWorldActor.tell(new HelloWorldMsgProtocol.SayHello("World Again"));
	}

}
