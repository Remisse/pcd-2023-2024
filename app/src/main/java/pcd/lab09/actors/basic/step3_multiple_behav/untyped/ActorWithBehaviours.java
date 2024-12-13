package pcd.lab09.actors.basic.step3_multiple_behav.untyped;
import akka.actor.*;


public class ActorWithBehaviours extends AbstractActor {

	private int state;
	
	/* Base behaviour */
	
	public ActorWithBehaviours() {
		state = 0;
	}
	
	@Override
	public Receive createReceive() {
		return receiveBuilder()
				.match(MsgProtocol.MsgZero.class,this::onMsgZero)
				.build();
	}

	private void onMsgZero(MsgProtocol.MsgZero msg) {
		log("msgZero - state: " + state);
		state++;
		this.getContext().become(receiverBehaviourA());
	}


	/* Behaviour A */

	public Receive receiverBehaviourA() {
		return receiveBuilder()
				.match(MsgProtocol.MsgOne.class,this::onMsgOne)
				.build();
	}
	
	private void onMsgOne(MsgProtocol.MsgOne msg) {
		log("msgOne - state: " + state);	
		state++;

		this.getContext().become(receiverBehaviourB());
	}
	
	/* Behaviour B */
	
	public Receive receiverBehaviourB() {
		return receiveBuilder()
				.match(MsgProtocol.MsgTwo.class,this::onMsgTwo)
				.build();
	}

	private void onMsgTwo(MsgProtocol.MsgTwo msg) {
		log("msgTwo - state: " + state);		
		this.getContext().stop(this.getSelf());
	}


	private void log(String msg) {
		System.out.println("[ActorWithBehaviour] " + msg);
	}


	
}
