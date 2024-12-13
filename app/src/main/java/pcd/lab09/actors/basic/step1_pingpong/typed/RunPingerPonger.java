package pcd.lab09.actors.basic.step1_pingpong.typed;

import akka.actor.typed.ActorSystem;

public class RunPingerPonger {
  public static void main(String[] args) throws Exception  {

	  final ActorSystem<BootActor.BootMsg> bootActor =
			    ActorSystem.create(BootActor.create(), "boot");
	  bootActor.tell(new BootActor.BootMsg());
  }
}
