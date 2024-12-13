package pcd.lab09.actors.basic.step4_multiple_behav_stash.typed;

import akka.actor.typed.ActorSystem;

public class RunActorWithMultipleBehavioursAndStashing {
  public static void main(String[] args) throws Exception  {

	  final ActorSystem<MsgProtocol.BaseMsg> myActor =
			    ActorSystem.create(BaseBehaviourWithStashing.create(0), "myActor");

	  /* 
	   * The following two msgs are not handled in the initial behaviour.
	   * However they are not lost, they are stashed
	   *  
	   */	  
	  myActor.tell(new MsgProtocol.MsgOne());  
	  myActor.tell(new MsgProtocol.MsgTwo());  
	  
	  /* Give time to the logging system to setup */
	  Thread.sleep(1000);

	  /* 
	   * The next msg will cause a transition to the new behaviour, 
	   * where the stashed msgs will be unstashed and processed.  
	   */
	  myActor.tell(new MsgProtocol.MsgZero()); 
  
	  System.out.println("done");
  }
}
