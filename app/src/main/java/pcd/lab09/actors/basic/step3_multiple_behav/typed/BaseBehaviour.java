package pcd.lab09.actors.basic.step3_multiple_behav.typed;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;


public class BaseBehaviour extends AbstractBehavior<MsgProtocol.BaseMsg> {

	private int initialState;
	
	private BaseBehaviour(ActorContext<MsgProtocol.BaseMsg> context, int initialState) {
		super(context);
		this.initialState = initialState;
	}
	
	@Override
	public Receive<MsgProtocol.BaseMsg> createReceive() {
		return newReceiveBuilder()
				.onMessage(MsgProtocol.MsgZero.class,this::onMsgZero)
				.build();
	}

	private Behavior<MsgProtocol.BaseMsg> onMsgZero(MsgProtocol.MsgZero msg) {
		this.getContext().getLog().info("msgZero - state: " + initialState);		
		return Behaviors.setup(context -> new BehaviourA(context, initialState + 1));
	}

	/* behaviour A */
	
	class BehaviourA extends AbstractBehavior<MsgProtocol.BaseMsg> {

		private int localState;
		
		private BehaviourA(ActorContext<MsgProtocol.BaseMsg> context, int localState) {
			super(context);
			this.localState = localState;
		}

		@Override
		public Receive<MsgProtocol.BaseMsg> createReceive() {
			return newReceiveBuilder()
					.onMessage(MsgProtocol.MsgOne.class, this::onMsgOne)
					.build();
		}	

		private Behavior<MsgProtocol.BaseMsg> onMsgOne(MsgProtocol.MsgOne msg) {
			this.getContext().getLog().info("msgOne - state: " + localState);		
			return Behaviors.setup(context -> new BehaviourB(context, localState + 1));
		}
	}

	/* behaviour B */
	
	class BehaviourB extends AbstractBehavior<MsgProtocol.BaseMsg> {

		private int localState;

		private BehaviourB(ActorContext<MsgProtocol.BaseMsg> context, int localState) {
			super(context);
			this.localState = localState;
		}

		@Override
		public Receive<MsgProtocol.BaseMsg> createReceive() {
			return newReceiveBuilder()
					.onMessage(MsgProtocol.MsgTwo.class, this::onMsgTwo)
					.build();
		}	

		private Behavior<MsgProtocol.BaseMsg> onMsgTwo(MsgProtocol.MsgTwo msg) {
			this.getContext().getLog().info("msgTwo - state: " + localState);		
			return Behaviors.stopped();
		}
	}
	
	public static Behavior<MsgProtocol.BaseMsg> create(int initialState) {
		return Behaviors.setup(context -> new BaseBehaviour(context, initialState));
	}


	
}
