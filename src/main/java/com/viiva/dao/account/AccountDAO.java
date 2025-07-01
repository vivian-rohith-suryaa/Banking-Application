package com.viiva.dao.account;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.viiva.exceptions.DBException;
import com.viiva.pojo.account.Account;
import com.viiva.pojo.account.AccountStatus;
import com.viiva.pojo.account.AccountType;
import com.viiva.pojo.request.Request;
import com.viiva.util.DBUtil;

public class AccountDAO {
	
	public Map<String,Object> createAccount(Request request){
		String query = "INSERT INTO account(customer_id,branch_id,type,balance,status,created_time) VALUES (?,?,?,?,?,?)";
		
		try (PreparedStatement pstmt = DBUtil.prepareWithKeys(DBUtil.getConnection(), query)) {
			pstmt.setLong(1, request.getCustomerId());
			pstmt.setLong(2, request.getBranchId());
			pstmt.setString(3, request.getAccountType().name());
			pstmt.setDouble(4, request.getBalance());
			pstmt.setString(5, AccountStatus.ACTIVE.name());
			pstmt.setLong(6, System.currentTimeMillis());
			
			int rows = DBUtil.executeUpdate(pstmt);
			if (rows > 0) {
				try (ResultSet rs = pstmt.getGeneratedKeys()) {
					if (rs.next()) {
						long accountId = rs.getLong(1);
						Map<String, Object> result = new HashMap<>();
						result.put("accountId", accountId);
						result.put("customerId", request.getCustomerId());
						return result;
					}
				}
			}
			return null;
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DBException("Error occurred while creating account request", e);
		}
	}
	
	public Account getAccountById(long accountId) {
		String query = "SELECT account_id,customer_id,branch_id,type,balance,status,pin FROM account WHERE account_id = ? FOR UPDATE";
		
		try (PreparedStatement pstmt = DBUtil.prepare(DBUtil.getConnection(), query)) {

			pstmt.setLong(1, accountId);

			try (ResultSet rs = DBUtil.executeQuery(pstmt)) {
				while (rs.next()) {
					Account account = new Account();
					account.setAccountId(rs.getLong("account_id"));
					account.setCustomerId(rs.getLong("customer_id"));
					account.setBranchId(rs.getLong("branch_id"));
					account.setAccountType(AccountType.valueOf(rs.getString("type")));
					account.setBalance(rs.getDouble("balance"));
					account.setStatus(AccountStatus.valueOf(rs.getString("status")));
					account.setPin(rs.getString("pin"));
					
					return account;
				}
				return null;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DBException("Error occurred while fetching account", e);
		}
	}
	
	public Account updateAccountById(Account account) {
		String query = "UPDATE account SET pin=?,modified_time=?, modified_by=? WHERE account_id = ?";
		try (PreparedStatement pstmt = DBUtil.prepare(DBUtil.getConnection(), query)) {
			pstmt.setString(1, account.getPin());
			pstmt.setLong(2, System.currentTimeMillis());
			pstmt.setLong(3, account.getModifiedBy());
			pstmt.setLong(4, account.getAccountId());
			
			int rows = DBUtil.executeUpdate(pstmt);
			if (rows > 0) {
				Account updatedAccount = getAccountById(account.getAccountId());
				return updatedAccount;
			}
			return null;
			
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DBException("Error occurred while updating account", e);
		}
	}
	
	public List<Account> getUserAccounts(long customerId){
		String query = "SELECT account_id,customer_id,branch_id,type,balance,status FROM account WHERE customer_id = ?";
		List<Account> accounts = new ArrayList<Account>();
		try (PreparedStatement pstmt = DBUtil.prepare(DBUtil.getConnection(), query)) {

			pstmt.setLong(1, customerId);

			try (ResultSet rs = DBUtil.executeQuery(pstmt)) {
				while (rs.next()) {
					Account account = new Account();
					account.setAccountId(rs.getLong("account_id"));
					account.setCustomerId(rs.getLong("customer_id"));
					account.setBranchId(rs.getLong("branch_id"));
					account.setAccountType(AccountType.valueOf(rs.getString("type")));
					account.setBalance(rs.getDouble("balance"));
					account.setStatus(AccountStatus.valueOf(rs.getString("status")));
					
					accounts.add(account);
				}
				return accounts;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DBException("Error occurred while fetching account", e);
		}
	}
	
	public boolean updateBalance(Account account) {
	    String query = "UPDATE account SET balance = ? WHERE account_id = ?";
	    try (PreparedStatement pstmt = DBUtil.prepare(DBUtil.getConnection(), query)) {

	        pstmt.setDouble(1, account.getBalance());
	        pstmt.setLong(2, account.getAccountId());

	        int rows = DBUtil.executeUpdate(pstmt);
	        return rows > 0;

	    } catch (SQLException e) {
	        throw new DBException("Failed to update account balance", e);
	    }
	}
	
	public List<Account> getAllAccounts(long branchId, byte role, Map<String, String> filters) {
		List<Account> accounts = new ArrayList<>();

		int page = 1;
		int limit = 10;
		boolean usePagination = true;

		if (filters != null) {
			if (filters.containsKey("page")) {
				try {
					page = Integer.parseInt(filters.get("page"));
				} catch (NumberFormatException e) {
					page = 1;
				}
				filters.remove("page");
			}
			if (filters.containsKey("limit")) {
				try {
					limit = Integer.parseInt(filters.get("limit"));
					if (limit == -1) {
						usePagination = false;
					}
				} catch (NumberFormatException e) {
					limit = 10;
				}
				filters.remove("limit");
			}
		}

		int offset = (page - 1) * limit;

		StringBuilder query = new StringBuilder(
			"SELECT account_id, customer_id, branch_id, type, balance, status FROM account"
		);

		if (role == 2 || role == 3) {
			query.append(" WHERE branch_id = ?");
		} else {
			query.append(" WHERE 1=1");
		}

		if (filters != null) {
			if (filters.containsKey("status")) {
				query.append(" AND status = ?");
			}
			if (filters.containsKey("type")) {
				query.append(" AND type = ?");
			}
			if (filters.containsKey("customerId")) {
				query.append(" AND customer_id = ?");
			}
		}

		query.append(" ORDER BY account_id DESC");
		if (usePagination) {
			query.append(" LIMIT ? OFFSET ?");
		}

		try (PreparedStatement pstmt = DBUtil.prepare(DBUtil.getConnection(), query.toString())) {
			int index = 1;

			if (role < 4) {
				pstmt.setLong(index++, branchId);
			}

			if (filters != null) {
				if (filters.containsKey("status")) {
					pstmt.setString(index++, filters.get("status"));
				}
				if (filters.containsKey("type")) {
					pstmt.setString(index++, filters.get("type"));
				}
				if (filters.containsKey("customerId")) {
					pstmt.setLong(index++, Long.parseLong(filters.get("customerId")));
				}
			}

			if (usePagination) {
				pstmt.setInt(index++, limit);
				pstmt.setInt(index, offset);
			}

			try (ResultSet rs = DBUtil.executeQuery(pstmt)) {
				while (rs.next()) {
					Account acc = new Account();
					acc.setAccountId(rs.getLong("account_id"));
					acc.setCustomerId(rs.getLong("customer_id"));
					acc.setBranchId(rs.getLong("branch_id"));
					acc.setAccountType(AccountType.valueOf(rs.getString("type")));
					acc.setBalance(rs.getDouble("balance"));
					acc.setStatus(AccountStatus.valueOf(rs.getString("status")));
					accounts.add(acc);
				}
			}

			return accounts;
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DBException("Error occurred while fetching accounts.", e);
		}
	}


}
