package com.viiva.dao.request;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.viiva.dao.account.AccountDAO;
import com.viiva.exceptions.DBException;
import com.viiva.pojo.request.RequestStatus;
import com.viiva.pojo.account.AccountType;
import com.viiva.pojo.request.Request;
import com.viiva.util.BasicUtil;
import com.viiva.util.DBUtil;

public class RequestDAO {
	
	public Map<String, Object> createRequest(Request request){
		
		String query = "INSERT INTO request(customer_id,branch_id,account_type,balance,status,created_time) VALUES (?,?,?,?,?,?)";
		
		try (PreparedStatement pstmt = DBUtil.prepareWithKeys(DBUtil.getConnection(), query)) {
			pstmt.setLong(1, request.getCustomerId());
			pstmt.setLong(2, request.getBranchId());
			pstmt.setString(3, request.getAccountType().name());
			pstmt.setDouble(4, request.getBalance());
			pstmt.setString(5, request.getStatus().name());
			pstmt.setLong(6, System.currentTimeMillis());
			
			int rows = DBUtil.executeUpdate(pstmt);
			if (rows > 0) {
				try (ResultSet rs = pstmt.getGeneratedKeys()) {
					if (rs.next()) {
						long requestId = rs.getLong(1);
						Map<String, Object> result = new HashMap<>();
						result.put("requestId", requestId);
						result.put("customerId", request.getCustomerId());
						result.put("status", request.getStatus().name());
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
	
	public Request getRequestById(long requestId) {
		String query = "SELECT request_id,customer_id,branch_id,account_type,balance,status,remarks FROM request WHERE request_id = ?";
		
		try (PreparedStatement pstmt = DBUtil.prepare(DBUtil.getConnection(), query)) {

			pstmt.setLong(1, requestId);

			try (ResultSet rs = DBUtil.executeQuery(pstmt)) {
				while (rs.next()) {
					Request request = new Request();
					request.setRequestId(rs.getLong("request_id"));
					request.setCustomerId(rs.getLong("customer_id"));
					request.setBranchId(rs.getLong("branch_id"));
					request.setAccountType(AccountType.valueOf(rs.getString("account_type")));
					request.setBalance(rs.getDouble("balance"));
					request.setStatus(RequestStatus.valueOf(rs.getString("status")));
					request.setRemarks(rs.getString("remarks"));
					
					return request;
				}
				return null;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DBException("Error occurred while fetching request", e);
		}
	}
	
	public Map<String,Object> updateRequest(Request request) {
		String query = "UPDATE request SET status=?,remarks=?,modified_time=?,modified_by=? WHERE request_id=?";
		
		try (PreparedStatement pstmt = DBUtil.prepare(DBUtil.getConnection(), query)) {
			
			pstmt.setString(1,request.getStatus().name());
			pstmt.setString(2, request.getRemarks());
			pstmt.setLong(3, System.currentTimeMillis());
			pstmt.setLong(4, request.getModifiedBy());
			pstmt.setLong(5, request.getRequestId());
			
			int rows = DBUtil.executeUpdate(pstmt);
			if (rows > 0) {
				 Map<String,Object> result = new HashMap<>();
		            result.put("requestId", request.getRequestId());
		            result.put("customerId", request.getCustomerId());
		            result.put("status", request.getStatus());
		            result.put("remarks", request.getRemarks());

		            if ("APPROVED".equals(request.getStatus().name())) {
		                AccountDAO accountDao = new AccountDAO();
		                Map<String,Object> account = accountDao.createAccount(request);

		                if (BasicUtil.isNull(account)) {
		                    throw new DBException("Account creation failed for approved request.");
		                }

		                result.put("accountId", account.get("accountId"));
		            }

		            return result;
			}
			return null;
			
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DBException("Error occurred while updating request", e);
		}
	}

	public List<Request> getBranchRequests(long branchId) {
		String query = "SELECT request_id, customer_id, branch_id, account_type, balance, status, remarks FROM request WHERE branch_id = ?";
	    List<Request> requests = new ArrayList<>();

	    try (PreparedStatement pstmt = DBUtil.prepare(DBUtil.getConnection(), query)) {
	        pstmt.setLong(1, branchId);

	        try (ResultSet rs = DBUtil.executeQuery(pstmt)) {
	            while (rs.next()) {
	                Request req = new Request();
	                req.setRequestId(rs.getLong("request_id"));
	                req.setCustomerId(rs.getLong("customer_id"));
	                req.setBranchId(rs.getLong("branch_id"));
	                req.setAccountType(AccountType.valueOf(rs.getString("account_type")));
	                req.setBalance(rs.getDouble("balance"));
	                req.setStatus(RequestStatus.valueOf(rs.getString("status")));
	                req.setRemarks(rs.getString("remarks"));
	                requests.add(req);
	            }
	        }

	        return requests;
	    } catch (SQLException e) {
	        e.printStackTrace();
	        throw new DBException("Error fetching requests for branch.", e);
	    }
	}
}
