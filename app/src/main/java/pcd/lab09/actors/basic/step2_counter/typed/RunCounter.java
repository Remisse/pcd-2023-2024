package pcd.lab09.actors.basic.step2_counter.typed;

import akka.actor.typed.ActorSystem;
import pcd.lab09.actors.basic.step2_counter.typed.MainActorBehaviour.BootMsg;

public class RunCounter {
		
  public static void main(String[] args) throws Exception  {

	  final ActorSystem<BootMsg> bootActor =
			    ActorSystem.create(MainActorBehaviour.create(), "boot");

	  bootActor.tell(new BootMsg());
  }
}
