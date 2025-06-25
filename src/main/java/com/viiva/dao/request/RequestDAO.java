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
		
		request.setStatus(RequestStatus.PENDING);
		
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

	public List<Request> getAllRequests(byte role, Long branchId, Map<String, String> filters) throws Exception {
	    List<Request> requests = new ArrayList<>();
	    
	    int page = 1;
	    int limit = 10;

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
	            } catch (NumberFormatException e) {
	                limit = 10;
	            }
	            filters.remove("limit");
	        }
	    }

	    int offset = (page - 1) * limit;

	    StringBuilder query = new StringBuilder(
	        "SELECT r.request_id,r.customer_id, r.account_type, r.status, r.balance,r.remarks, r.created_time, " +
	        "r.modified_time, r.modified_by, b.branch_id " +
	        "FROM request r INNER JOIN branch b ON r.branch_id = b.branch_id"
	    );

	    if (role == 2 || role == 3) {
	        query.append(" WHERE b.branch_id = ?");
	    } else {
	        query.append(" WHERE 1=1");
	    }

	    if (filters != null) {
	        if (filters.containsKey("status")) {
	            query.append(" AND r.status = ?");
	        }
	        if (filters.containsKey("branchId") && role == 4) {
	            query.append(" AND a.branch_id = ?");
	        }
	        if (filters.containsKey("customerId")) {
				query.append(" AND customer_id = ?");
			}
	        
	    }
	    query.append(" ORDER BY r.request_id DESC LIMIT ? OFFSET ?");

	    try (PreparedStatement pstmt = DBUtil.prepare(DBUtil.getConnection(), query.toString())) {
	        int index = 1;

	        if (role == 2 || role == 3) {
	            pstmt.setLong(index++, branchId);
	        }

	        if (filters != null) {
	            if (filters.containsKey("status")) {
	                pstmt.setString(index++, filters.get("status"));
	            }
	            if (filters.containsKey("branchId") && role == 4) {
	                pstmt.setLong(index++, Long.parseLong(filters.get("branchId")));
	            }
	            if (filters.containsKey("customerId")) {
					pstmt.setLong(index++, Long.parseLong(filters.get("customerId")));
				}
	        }
	        pstmt.setInt(index++, limit);
	        pstmt.setInt(index, offset);

	        try (ResultSet rs = DBUtil.executeQuery(pstmt)) {
	            while (rs.next()) {
	                Request req = new Request();
	                req.setRequestId(rs.getLong("request_id"));
	                req.setCustomerId(rs.getLong("customer_id"));
	                req.setBranchId(rs.getLong("branch_id"));
	                req.setAccountType(AccountType.valueOf(rs.getString("account_type")));
	                req.setStatus(RequestStatus.valueOf(rs.getString("status")));
	                req.setBalance(rs.getDouble("balance"));
	                req.setRemarks(rs.getString("remarks"));
	                req.setCreatedTime(rs.getLong("created_time"));
	                req.setModifiedTime(rs.getLong("modified_time"));
	                req.setModifiedBy(rs.getLong("modified_by"));
	                 
	                requests.add(req);
	            }
	        }

	        return requests;
	    } catch (SQLException e) {
	        e.printStackTrace();
	        throw new DBException("Error occurred while fetching requests.", e);
	    }
	}

}
