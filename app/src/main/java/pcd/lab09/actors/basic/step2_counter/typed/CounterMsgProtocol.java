package pcd.lab09.actors.basic.step2_counter.typed;

import akka.actor.typed.ActorRef;

public class CounterMsgProtocol {

	static public interface CounterMsg {}

	static public interface CounterUserMsg {}

	static public class IncMsg implements CounterMsg {}
	
	static public class GetValueMsg implements CounterMsg {
		public final ActorRef<CounterUserMsg> replyTo;
		public GetValueMsg(ActorRef<CounterUserMsg> replyTo) {
			this.replyTo = replyTo;
		}
	}

	static public class CounterValueMsg implements CounterUserMsg {
		public final int value;
		public CounterValueMsg(int value) {
			this.value = value;
		}
	}

}
