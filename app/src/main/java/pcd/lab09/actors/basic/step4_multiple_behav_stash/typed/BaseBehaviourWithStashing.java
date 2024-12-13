package pcd.lab09.actors.basic.step4_multiple_behav_stash.typed;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import akka.actor.typed.javadsl.StashBuffer;

public class BaseBehaviourWithStashing extends AbstractBehavior<MsgProtocol.BaseMsg> {

	private int initialState;
	private StashBuffer<MsgProtocol.BaseMsg> stashBuffer;
	
	private BaseBehaviourWithStashing(ActorContext<MsgProtocol.BaseMsg> context, 
													int initialState,
													StashBuffer<MsgProtocol.BaseMsg> stash) {
		super(context);
		this.initialState = initialState;
		this.stashBuffer = stash;
	}
	

	public static Behavior<MsgProtocol.BaseMsg> create(int initialState) {
		return Behaviors.withStash(100,
				stash ->  Behaviors.setup(
						context -> new BaseBehaviourWithStashing(context, initialState, stash)));
	}

	@Override
	public Receive<MsgProtocol.BaseMsg> createReceive() {
		return newReceiveBuilder()
				.onMessage(MsgProtocol.MsgZero.class,this::onMsgZero)
				.onMessage(MsgProtocol.BaseMsg.class,this::onAnyMsg)
				.build();
	}

	
	private Behavior<MsgProtocol.BaseMsg> onMsgZero(MsgProtocol.MsgZero msg) {
		this.getContext().getLog().info("msgZero - state: " + initialState);		
		return stashBuffer.unstashAll(
					Behaviors.setup(context -> new BehaviourA(context, initialState + 1))
			   );
	}

	private Behavior<MsgProtocol.BaseMsg> onAnyMsg(MsgProtocol.BaseMsg msg) {
		this.getContext().getLog().info("stashing msg - state " + initialState);	
		stashBuffer.stash(msg);
		return this;
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
	
	
}
