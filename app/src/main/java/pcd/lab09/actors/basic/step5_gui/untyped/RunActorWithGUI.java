package pcd.lab09.actors.basic.step5_gui.untyped;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;

public class RunActorWithGUI {
  public static void main(String[] args) throws Exception  {
    ActorSystem system = ActorSystem.create("MySystem");
    
    ActorRef act = system.actorOf(Props.create(ViewActor.class));
	new ViewFrame(act).display();
  }
}
