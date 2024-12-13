package pcd.lab09.actors.basic.step0_hello.untyped;

public class HelloWorldMsgProtocol {

	/* messages types */
	
	public static class SayHello {
		
		private final String content;

		public SayHello(String content) {
			this.content = content;
		}
		
		public String getContent() {
			return content;
		}
	}


}
