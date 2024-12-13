package pcd.lab09.actors.basic.step2_counter.untyped;

import akka.actor.*;

public class RunActorSystem {
		
  public static void main(String[] args) throws Exception  {

	final ActorSystem system = ActorSystem.create("my-actor-system");		
	final ActorRef bootActor =  system.actorOf(Props.create(MainActor.class), "main-actor");
	bootActor.tell(new MainActor.BootMsg(), null);
  }
}
