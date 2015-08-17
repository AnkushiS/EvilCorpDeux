import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.Properties;

public class DBProcess {

	public static Connection dbConfig() throws SQLException {

		String url = "jdbc:oracle:thin:testuser/password@localhost";
		Properties props = new Properties();
		props.setProperty("user", "testdb");
		props.setProperty("password", "password");
		Connection conn = DriverManager.getConnection(url, props);
		return conn;
	}

	
	public static int checkAccExists(String name){
		int count=0;
		try {
			name = name.toLowerCase();
			Connection conn = dbConfig();
			
			String sql = "select count(*) from accounts where name=" + "'" +name + "'";
			PreparedStatement preStatement = conn.prepareStatement(sql);
			ResultSet result = preStatement.executeQuery();
			
			while(result.next()){
				count = result.getInt(1);
			}			
		}catch(SQLException e){
			System.out.println("In CheckAccExists: " + e.getMessage());
		}
		return count;
	}
	
	
	public static void updateDbAllTran(Lists ll) {

		try {
			Connection conn = dbConfig();

			for (Account ltranDet : ll.getTranAcc()) {
				String sql = "insert into transactions (tran_id, acct_id, Amount, Tran_date, tran_type) values "
						+ "(tran_id_seq.nextval , "
						+ "'"
						+ ltranDet.getAccNum()
						+ "'"
						+ ","
						+ ltranDet.getAmount()
						+ ","
						+ "TO_DATE("
						+ "'"
						+ Processing.processReverseDate(ltranDet.getDate())
						+ "'"
						+ ","
						+ "'MM/DD/YYYY')"
						+ ","
						+ "'"
						+ ltranDet.getTranType() + "')";

				PreparedStatement preStatement1 = conn.prepareStatement(sql);
				preStatement1.executeQuery();

			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void updateTotalBalDB(Lists ll) {

		try {
			Connection conn = dbConfig();

			for (Account lttBal : ll.getInitAcc()) {
				String sql = "update accounts set init_bal="
						+ lttBal.getInitBal() + "where acct_id="
						+ "'" + lttBal.getAccNum() + "'";
				PreparedStatement preStatement = conn.prepareStatement(sql);
				preStatement.executeQuery();
				conn.commit();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	public static void removeFromDB(String remv_Acc_num) {
		try {
			Connection conn = dbConfig();
			String sql = "select init_bal from Accounts where acct_id=" + "'"
					+ remv_Acc_num + "'";
			PreparedStatement accountBal = conn.prepareStatement(sql);
			ResultSet balance = accountBal.executeQuery();

			while (balance.next()) {
				if (balance.getDouble("init_bal") == 0) {
					// remove the account
					String sqlRmv = "delete from accounts where acct_id=" + "'"
							+ remv_Acc_num + "'";
					PreparedStatement accountRmvd = conn
							.prepareStatement(sqlRmv);
					accountRmvd.executeQuery();
					conn.commit();
				} else {
					System.out.println("Cannot close the account# "
							+ remv_Acc_num);
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void addAccount(String numIn, String name, double amount,
			Date dateIn) {
		try {
			Connection conn = dbConfig();
			String sql = "INSERT INTO accounts (ACCT_ID,NAME,Init_bal,birthdate) VALUES ( "
					+ "'"
					+ numIn
					+ "'"
					+ ", "
					+ "'"
					+ name
					+ "'"
					+ ", "
					+ amount
					+ ", "
					+ "TO_DATE("
					+ "'"
					+ Processing.processReverseDate(dateIn)
					+ "'"
					+ ","
					+ "'MM/DD/YYYY'))";
			PreparedStatement preStatement = conn.prepareStatement(sql);
			preStatement.executeQuery();
			conn.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
