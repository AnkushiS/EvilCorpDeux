import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Processing {

	public static void populateListDB(Lists ll) {
		try {
			Connection conn = DBProcess.dbConfig();
			String sql = "select Acct_id, name, init_bal, birthdate, acct_type from Accounts";
			PreparedStatement preStatement = conn.prepareStatement(sql);
			ResultSet result = preStatement.executeQuery();
			while (result.next()) {
				Account acc = new Account();
				acc.setAccNum(result.getString("acct_id"));
				acc.setName(result.getString("name"));
				acc.setInitBal(result.getDouble("Init_bal"));
				acc.setBirthdate(result.getDate("birthdate"));
				acc.setAccType(result.getString("acct_type"));
				ll.setInitAcc(acc);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	public static void sortList(Lists ll) {
		for (int i = 0; i < ll.getTranAcc().size(); i++) {
			for (int j = i; j < ll.getTranAcc().size(); j++) {
				// Sort the date first.
				if (ll.getTranAcc().get(i).getDate()
						.compareTo(ll.getTranAcc().get(j).getDate()) > 0) {
					Account temp3 = ll.getTranAcc().get(i);
					ll.getTranAcc().set(i, ll.getTranAcc().get(j));
					ll.getTranAcc().set(j, temp3);
				}

				// If the dates are the same, compare the transaction types and
				// amounts.
				if (ll.getTranAcc().get(i).getDate()
						.compareTo(ll.getTranAcc().get(j).getDate()) == 0) {
					// If the second transaction type is "add an account", swap
					// the order.
					if (ll.getTranAcc().get(j).getTranType().equals("A")) {
						Account temp1 = ll.getTranAcc().get(i);
						ll.getTranAcc().set(i, ll.getTranAcc().get(j));
						ll.getTranAcc().set(j, temp1);

						// If the second transaction type is "deposit" and the
						// first one is any of the other types, keep the same
						// order.
					} else if ((!ll.getTranAcc().get(i).getTranType()
							.equals("DEP"))
							&& ll.getTranAcc().get(j).getTranType()
									.equals("DEP")) {
						continue;

						// if the first transaction type is "deposit" and the
						// second one is any of the other types, swap the order.
					} else if (ll.getTranAcc().get(i).getTranType()
							.equals("DEP")
							&& (!ll.getTranAcc().get(j).getTranType()
									.equals("DEP"))) {
						Account temp2 = ll.getTranAcc().get(i);
						ll.getTranAcc().set(i, ll.getTranAcc().get(j));
						ll.getTranAcc().set(j, temp2);

						// If both of the transactions are not "deposit", put
						// the one with larger amount first.
					} else if (ll.getTranAcc().get(i).getAmount() < ll
							.getTranAcc().get(j).getAmount()) {
						Account temp3 = ll.getTranAcc().get(i);
						ll.getTranAcc().set(i, ll.getTranAcc().get(j));
						ll.getTranAcc().set(j, temp3);
					}
				}
			}
		}
	}

	public static void calTotalBal(Lists ll) {
		
		for (Account ltranAcc : ll.getTranAcc()) {

			for (Account lnewAcc : ll.getInitAcc()) {
				
				if(ltranAcc.getTranType().equalsIgnoreCase("CS") || ltranAcc.getTranType().equalsIgnoreCase("SC")){
					continue;
				}
				if (ltranAcc.getAccNum().equals(lnewAcc.getAccNum())) {	
					lnewAcc.setInitBal(ltranAcc.getAmount()
							+ lnewAcc.getInitBal());
					if (lnewAcc.getInitBal() < 0) {
						if (lnewAcc.getAccType().equalsIgnoreCase("Savings")) {
							lnewAcc.setInitBal(lnewAcc.getInitBal() + (-35));
						} else {
							double overdraftAmount = 0 - lnewAcc.getInitBal();
							lnewAcc.setInitBal(0);
							//savings account number 
							double savIdx = EvilAskApp.findIndex(lnewAcc.getAccNum() + "S", ll)[1];
							double savBal = EvilAskApp.findIndex(lnewAcc.getAccNum() + "S", ll)[0];
							ll.getInitAcc().get((int)savIdx).setInitBal(savBal-overdraftAmount-15);							
						}
					}

				}
				
			}
	
		}
		DBProcess.updateTotalBalDB(ll);
	}

	public static void removeAccount(String remv_Acc_num, Lists ll) {

		for (int i = 0; i < ll.getInitAcc().size(); i++) {
			if (ll.getInitAcc().get(i).getAccNum().equals(remv_Acc_num)) {
				ll.getInitAcc().remove(i);
				break;
			}
		}

		for (int i = 0; i < ll.getTranAcc().size(); i++) {
			if (ll.getTranAcc().get(i).getTranType().equalsIgnoreCase("R")) {
				ll.getTranAcc().remove(i);
				break;
			}
		}

		DBProcess.removeFromDB(remv_Acc_num);
	}

	public static String processReverseDate(Date date) {
		SimpleDateFormat formatDate = new SimpleDateFormat("M/d/yyyy");
		return formatDate.format(date);
	}

}
