package pcd.lab03.liveness.accounts_exercise;

public class RunTransactions {

	private static final int NUM_TRANSFER_AGENTS = 20;
	private static final int NUM_ACCOUNTS = 5;
	private static final int NUM_ITERATIONS = 10000000;

	
	public static void main(String[] args) {		
		
		AccountManager man = new AccountManager(NUM_ACCOUNTS,1000);
		
		for (int i = 0; i < NUM_TRANSFER_AGENTS; i++){
			new TransferAgent(man, NUM_ITERATIONS).start();
		}
	}
}
