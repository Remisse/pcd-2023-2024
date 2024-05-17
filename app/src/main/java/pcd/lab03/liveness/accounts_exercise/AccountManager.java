package pcd.lab03.liveness.accounts_exercise;

public class AccountManager {
	
	private final Account[] accounts;

	public AccountManager(int nAccounts, int amount){
		accounts = new Account[nAccounts];
		for (int i = 0; i < accounts.length; i++){
			accounts[i] = new Account(amount);
		}
	}
	
	public void transferMoney(int from,	int to, int amount) throws InsufficientBalanceException {
		throw new RuntimeException("To be implemented");
	}
	
	public int getNumAccounts() {
		return accounts.length;
	}
}

