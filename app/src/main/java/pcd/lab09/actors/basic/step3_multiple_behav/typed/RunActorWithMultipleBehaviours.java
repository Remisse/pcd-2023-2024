package pcd.lab09.actors.basic.step3_multiple_behav.typed;

import akka.actor.typed.ActorSystem;

public class RunActorWithMultipleBehaviours {
  public static void main(String[] args) throws Exception  {

	  final ActorSystem<MsgProtocol.BaseMsg> myActor =
			    ActorSystem.create(BaseBehaviour.create(0), "myActor");

	  /* The next two msgs will be unhandled, because not managed in the initial behaviour */
	  myActor.tell(new MsgProtocol.MsgOne());  
	  myActor.tell(new MsgProtocol.MsgTwo());  
	  
	  /* give time to the logging system to setup */
	  Thread.sleep(1000);

	  /* The next msgs will be managed and will cause a transition to new behaviours, up to stopped */
	  myActor.tell(new MsgProtocol.MsgZero()); 
	  myActor.tell(new MsgProtocol.MsgOne()); 
	  myActor.tell(new MsgProtocol.MsgTwo()); 
  
	  /* these msgs will be not delivered since the actor stopped */
	  myActor.tell(new MsgProtocol.MsgZero()); 
	  myActor.tell(new MsgProtocol.MsgOne()); 
	  myActor.tell(new MsgProtocol.MsgTwo()); 

	  System.out.println("done");
  }
}
