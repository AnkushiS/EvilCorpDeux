import java.util.Date;
import java.util.Scanner;

public class EvilAskApp {

	public static void main(String[] args) {
		Lists ll = new Lists();
		String minOne = "-1";
		Scanner scan = new Scanner(System.in);

		System.out.println("Welcome to the Corp Savings and Loan");

		// create the initial list , populate from DB.
		Processing.populateListDB(ll);

		for (Account lnewAcc : ll.getInitAcc()) {
			System.out.println("Account#: " + lnewAcc.getAccNum() + "\n"
					+ "Name: " + lnewAcc.getName() + "\n" + "Balance: "
					+ lnewAcc.getInitBal() + "\n");
		}

		String tranType = Validator
				.getType(
						scan,
						"Enter a transaction type (Add an account[A], Check[C], "
								+ "Debit card[D], Deposit[DEP], "
								+ "Remove an account[R], Transfer[T], Withdrawal[W] or -1 to finish: ");
		while (!tranType.equals(minOne)) {

			Account tranAcc = new Account();
			tranAcc.setTranType(tranType);

			boolean isValid = false;
			String numIn = "";
			double amount = 0.0;
			Date dateIn = new Date();
			String accType = "Checking";

			if (tranAcc.getTranType().equalsIgnoreCase("C")
					|| tranAcc.getTranType().equalsIgnoreCase("D")
					|| tranAcc.getTranType().equalsIgnoreCase("W")) {
				numIn = Validator.getString(scan, "Enter the account#: ");
				while (isValid == false) {
					for (int i = 0; i < ll.getInitAcc().size(); i++) {
						if (ll.getInitAcc().get(i).getAccNum().equals(numIn)) {
							isValid = true;
							break;
						}
					}
					if (isValid == false) {
						System.out.println("account not in the system");
						numIn = Validator.getString(scan,
								"Enter the account#: ");

					}
				}
				tranAcc.setAccNum(numIn);

				amount = -Validator.getDouble(scan, "Enter the amount: ", 0);
				tranAcc.setAmount(amount);
				dateIn = Validator
						.getDate(scan, "Enter the date (mm/dd/yyy): ");
				tranAcc.setDate(dateIn);
				tranAcc.setAccType(accType);
				ll.setTranAcc(tranAcc);

			} else if (tranAcc.getTranType().equalsIgnoreCase("DEP")) {
				numIn = Validator.getString(scan, "Enter the account#: ");

				while (isValid == false) {
					for (int i = 0; i < ll.getInitAcc().size(); i++) {
						if (ll.getInitAcc().get(i).getAccNum().equals(numIn)) {
							isValid = true;
							break;
						}
					}
					if (isValid == false) {
						System.out.println("account not in the system");
						numIn = Validator.getString(scan,
								"Enter the account#: ");

					}
				}
				tranAcc.setAccNum(numIn);
				amount = Validator.getDouble(scan, "Enter the amount: ", 0);
				tranAcc.setAmount(amount);
				dateIn = Validator
						.getDate(scan, "Enter the date (mm/dd/yyy): ");
				tranAcc.setDate(dateIn);
				tranAcc.setAccType(accType);
				ll.setTranAcc(tranAcc);
			} else if (tranAcc.getTranType().equalsIgnoreCase("R")) {
				numIn = Validator.getString(scan, "Enter the account#: ");

				while (isValid == false) {
					for (int i = 0; i < ll.getInitAcc().size(); i++) {
						if (ll.getInitAcc().get(i).getAccNum().equals(numIn)) {
							isValid = true;
							break;
						}
					}
					if (isValid == false) {
						System.out.println("account not in the system");
						numIn = Validator.getString(scan,
								"Enter the account#: ");

					}
				}
				tranAcc.setAccNum(numIn);
				ll.setTranAcc(tranAcc);
				Processing.calTotalBal(ll);
				Processing.removeAccount(numIn, ll);
			} else if (tranAcc.getTranType().equalsIgnoreCase("A")) {
				String name = Validator.getString(scan, "Enter the name: ");
				int accCount = DBProcess.checkAccExists(name);
				if (accCount == 0) {
					numIn = Validator.getString(scan, "Enter the account#: ");
					amount = Validator.getDouble(scan,
							"Enter the initial balance: ", 0);
					dateIn = Validator.getDate(scan,
							"Enter the birthdate (mm/dd/yyy): ");
					DBProcess.addAccount(numIn, name, amount, dateIn);
					Account lupdInit = new Account();
						lupdInit.setAccNum(numIn);
						lupdInit.setBirthdate(dateIn);
						lupdInit.setInitBal(amount);
						lupdInit.setAccType("Checking");
					
						ll.setInitAcc(lupdInit);
					
					
				} else if (accCount == 1) {
					String ySave = Validator
							.getString(scan,
									"Checking Account exists, Do you wish to open a Savings Account?? [Y/N]");
					if (ySave.equalsIgnoreCase("y")) {
						amount = Validator.getDouble(scan,
								"Enter the initial balance: ", 0);
						accType = "Savings";
						
						for (Account lBirth : ll.getInitAcc()) {
							if (lBirth.getName().equalsIgnoreCase(name)) {
								dateIn = lBirth.getBirthdate();
								numIn = lBirth.getAccNum() + "S";
							}
						}
						Account lupdInit = new Account();
						lupdInit.setAccNum(numIn);
						lupdInit.setBirthdate(dateIn);
						lupdInit.setInitBal(amount);
						lupdInit.setAccType("Savings");
					
						ll.setInitAcc(lupdInit);
					DBProcess.addAccount(numIn, name, amount, dateIn);
					} else {
						System.out
								.println("Thank you, You can add the savings account later");
						continue;
					}
				} else {
					System.out.println("You cannot add more accounts");
				}

				// sql to add the account
				
			} else if (tranAcc.getTranType().equalsIgnoreCase("T")) {
				String name = Validator.getString(scan, "Enter the name: ");
				int accCount = DBProcess.checkAccExists(name);
				if (accCount == 2) {
					amount = Validator.getDouble(scan,
							"Enter the amount to transfer: ", 0);
					String saveCheck = Validator
							.getString(scan,
									"Kind of transfer check-save[CS] / save-check [SC]? ");

					dateIn = Validator.getDate(scan,
							"Enter the date (mm/dd/yyy): ");

					// calculate total balance for transactions for the same
					// account before the transfer.
					Processing.calTotalBal(ll);
					if (saveCheck.equalsIgnoreCase("CS")) {
						if (numIn.endsWith("S")) {
							numIn = numIn.substring(0, numIn.length() - 1);
							tranType = saveCheck;		
							transfer(numIn, amount, saveCheck, ll);
							
						} else {
							tranType = saveCheck;
							transfer(numIn, amount, saveCheck, ll);
							
						}

					} else if (saveCheck.equalsIgnoreCase("SC")) {
						if (numIn.endsWith("S")) {
							tranType = saveCheck;
							transfer(numIn, amount, saveCheck, ll);
							
						} else {
							numIn = numIn + "S";
							tranType = saveCheck;
							transfer(numIn, amount, saveCheck, ll);
							
						}
					}
					
					tranAcc.setAccNum(numIn);
					tranAcc.setDate(dateIn);
					tranAcc.setAmount(amount);
					tranAcc.setTranType(tranType);
					ll.setTranAcc(tranAcc);
				} else {
					System.out.println("not enough accounts to transfer");
					break;
				}
			}
			tranType = Validator
					.getType(
							scan,
							"Enter a transaction type (Add an account[A], Check[C], "
									+ "Debit card[D], Deposit[DEP], "
									+ "Remove an account[R], Transfer[T], Withdrawal[W] or -1 to finish: ");

		}
		
		
		Processing.sortList(ll);
		
		Processing.calTotalBal(ll);

		for (Account lnewAcc : ll.getInitAcc()) {
			System.out.println("Account#: " + lnewAcc.getAccNum());
			System.out.println("Balance: " + lnewAcc.getInitBal() + "\n");
		}
		
		printAllTran(ll);
		DBProcess.updateDbAllTran(ll);

	}

	private static void transfer(String numIn, double amount,
			String transferType, Lists ll) {

		double[] result = findIndex(numIn, ll);
		
		System.out.println(result[0]);
		if (result[0] < amount) {
			System.out.println("not enough balance to transfer");
		} else {
			
						
			if (transferType.equalsIgnoreCase("CS")) {
				ll.getInitAcc().get((int) result[1]).setInitBal(result[0] - amount);
				// find account number
				String accNum = ll.getInitAcc().get((int) result[1])
						.getAccNum();
				// account number for savings is
				accNum += "S";
				double[] saveAcc = findIndex(accNum, ll);
				ll.getInitAcc().get((int) saveAcc[1])
						.setInitBal(saveAcc[0] + amount);
				System.out.println(ll.getInitAcc().get((int)saveAcc[1]).getInitBal());
				
			} else {
				ll.getInitAcc().get((int) result[1]).setInitBal(result[0] - amount);
				String accNum = ll.getInitAcc().get((int) result[1])
						.getAccNum();
				// account number for savings is
				accNum = accNum.substring(0, accNum.length() - 1);
				double[] chkAcc = findIndex(accNum, ll);
				ll.getInitAcc().get((int) chkAcc[1])
						.setInitBal(chkAcc[0] + amount);
				
			}
		}
		
	}

	public static double[] findIndex(String numIn, Lists ll) {

		double[] result = new double[2];
		double balNow = 0;
		double accIndex = 0;

		for (int i = 0; i < ll.getInitAcc().size(); i++) {
			if (ll.getInitAcc().get(i).getAccNum().equalsIgnoreCase(numIn)) {
				balNow = ll.getInitAcc().get(i).getInitBal();
				accIndex = i;
				break;
			}
		}
		result[0] = balNow;
		result[1] = accIndex;

		return result;
	}

	private static void printAllTran(Lists ll) {
		System.out.println("Printing the Transaction Summary");
		for (Account ltranAcc : ll.getTranAcc()) {
			for (Account lnewAcc : ll.getInitAcc()) {
				if (ltranAcc.getAccNum().equals(lnewAcc.getAccNum())) {
					System.out.println("\n");
					System.out.println("Account#: " + ltranAcc.getAccNum());
					System.out.println("Account Holder's Name: "
							+ lnewAcc.getName());
					System.out.println("Transaction Type: "
							+ ltranAcc.getTranType());
					System.out.println("Amount of Transaction: "
							+ ltranAcc.getAmount());
					System.out
							.println("Date of Transaction: "
									+ Processing.processReverseDate(ltranAcc
											.getDate()));
				}

			}
		}
	}

}
