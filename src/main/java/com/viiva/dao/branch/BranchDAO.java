package com.viiva.dao.branch;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.viiva.exceptions.DBException;
import com.viiva.pojo.branch.Branch;
import com.viiva.util.BasicUtil;
import com.viiva.util.DBUtil;

public class BranchDAO {

	private static final String BANK_CODE = "VIIV";
	private static final char RESERVED = '0';

	public Map<String, Object> addBranch(Branch branch) {

		String query = "INSERT INTO branch (branch_name,manager_id, ifsc_code, locality, district, state, created_time, modified_by) VALUES(?,?,?,?,?,?,?,?)";

		try (PreparedStatement pstmt = DBUtil.prepareWithKeys(DBUtil.getConnection(), query)) {
			
			pstmt.setString(1, branch.getBranchName());
			pstmt.setObject(2, branch.getManagerId());
			pstmt.setString(3, "");
			pstmt.setString(4, branch.getLocality());
			pstmt.setString(5, branch.getDistrict());
			pstmt.setString(6, branch.getState());
			pstmt.setLong(7, System.currentTimeMillis());
			pstmt.setLong(8, branch.getModifiedBy());

			int rows = DBUtil.executeUpdate(pstmt);
			if (rows > 0) {
				try (ResultSet rs = pstmt.getGeneratedKeys()) {
					if (rs.next()) {
						long branchId = rs.getLong(1);
						String ifscCode = generateIFSC(branchId);

						String updateQuery = "UPDATE branch SET ifsc_code = ? WHERE branch_id = ?";

						try (PreparedStatement updateStmt = DBUtil.prepareWithKeys(DBUtil.getConnection(),
								updateQuery)) {

							updateStmt.setString(1, ifscCode);
							updateStmt.setLong(2, branchId);
							int updateRow = DBUtil.executeUpdate(updateStmt);
							if (updateRow == 1) {
								Map<String, Object> result = new HashMap<>();
								result.put("branchId", branchId);

								return result;

							}

							return null;
						}

					}
				}
			}
			return null;

		} catch (SQLException e) {
			e.printStackTrace();
			throw new DBException("Error occurred while registering a branch.", e);
		}
	}

	public Branch updateBranch(Branch branch) {
		String query = "UPDATE branch SET branch_name=?,manager_id=?,locality=?,district=?,state=?,modified_time=?,modified_by=? WHERE branch_id=?";

		try (PreparedStatement pstmt = DBUtil.prepare(DBUtil.getConnection(), query)) {
			long now = System.currentTimeMillis();
			
			pstmt.setString(1, branch.getBranchName());
			pstmt.setObject(2, branch.getManagerId());
			pstmt.setString(3, branch.getLocality());
			pstmt.setString(4, branch.getDistrict());
			pstmt.setString(5, branch.getState());
			pstmt.setLong(6, now);
			pstmt.setLong(7, branch.getModifiedBy());
			pstmt.setLong(8, branch.getBranchId());

			int rows = DBUtil.executeUpdate(pstmt);
			if (rows > 0) {
				Branch updatedBranch = getBranchById(branch.getBranchId());
				return updatedBranch;
			}
			return null;	
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DBException("Error occurred while updating the branch details.", e);
		}
	}

	public Branch getBranchById(long branchId) {
		String query = "SELECT branch_id,branch_name,manager_id,ifsc_code,locality,district,state,created_time,modified_time,modified_by FROM branch WHERE branch_id = ?";
		try (PreparedStatement pstmt = DBUtil.prepare(DBUtil.getConnection(), query)) {
			pstmt.setLong(1, branchId);

			try (ResultSet rs = DBUtil.executeQuery(pstmt)) {
				while (rs.next()) {

					Branch branch = new Branch();
					branch.setBranchId(rs.getLong("branch_id"));
					branch.setBranchName(rs.getString("branch_name"));
					branch.setManagerId(rs.getLong("manager_id"));
					branch.setIfscCode(rs.getString("ifsc_code"));
					branch.setLocality(rs.getString("locality"));
					branch.setDistrict(rs.getString("district"));
					branch.setState(rs.getString("state"));
					branch.setCreatedTime(rs.getLong("created_time"));
	                branch.setModifiedTime(rs.getLong("modified_time"));
	                branch.setModifiedBy(rs.getLong("modified_by"));
					return branch;
				}
				return null;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DBException("Error occurred while fetching the branch details.", e);
		}
	}
	
	
	public List<Branch> getAllBranches(Map<String, String> filters) {
		List<Branch> branches = new ArrayList<>();
		
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
		
		StringBuilder query = new StringBuilder("SELECT branch_id,branch_name, manager_id, ifsc_code, locality, district, state, created_time, modified_time, modified_by FROM branch WHERE 1=1");
		if (filters != null) {
			if (filters.containsKey("ifsc_code")) {
				query.append(" AND ifsc_code = ?");
			}
			if (filters.containsKey("state")) {
				query.append(" AND state = ?");
			}
			if (filters.containsKey("district")) {
				query.append(" AND district = ?");
			}
			if (filters.containsKey("locality")) {
				query.append(" AND locality = ?");
			}
			if (filters.containsKey("branchId")) {
	            query.append(" AND branch_id = ?");
	        }
			
		}
		query.append(" ORDER BY branch_id DESC LIMIT ? OFFSET ?");

		try (PreparedStatement pstmt = DBUtil.prepare(DBUtil.getConnection(), query.toString())) {
			int index = 1;
			if (filters != null) {
				if (filters.containsKey("ifsc_code")) {
					pstmt.setString(index++, filters.get("ifsc_code"));
				}
				if (filters.containsKey("state")) {
					pstmt.setString(index++, filters.get("state"));
				}
				if (filters.containsKey("district")) {
					pstmt.setString(index++, filters.get("district"));
				}
				if (filters.containsKey("locality")) {
					pstmt.setString(index++, filters.get("locality"));
				}
				if (filters.containsKey("branchId")) {
					pstmt.setString(index++, filters.get("branchId"));
				}
			}
			
			pstmt.setInt(index++, limit);
	        pstmt.setInt(index, offset);

			try (ResultSet rs = DBUtil.executeQuery(pstmt)) {
				while (rs.next()) {
					Branch branch = new Branch();
					branch.setBranchId(rs.getLong("branch_id"));
					branch.setBranchName(rs.getString("branch_name"));
					branch.setManagerId(rs.getLong("manager_id"));
					branch.setIfscCode(rs.getString("ifsc_code"));
					branch.setLocality(rs.getString("locality"));
					branch.setDistrict(rs.getString("district"));
					branch.setState(rs.getString("state"));
					branch.setCreatedTime(rs.getLong("created_time"));
					branch.setModifiedTime(rs.getLong("modified_time"));
					branch.setModifiedBy(rs.getLong("modified_by"));
					branches.add(branch);
				}
			}
			return branches;
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DBException("Error fetching branch list", e);
		}
	}

	
	protected static String generateIFSC(long branchId) {
		if (!BasicUtil.isBlank(branchId)) {
			String branchCode = String.format("%06d", branchId);
			return BANK_CODE + RESERVED + branchCode;
		}
		return null;
	}

}
