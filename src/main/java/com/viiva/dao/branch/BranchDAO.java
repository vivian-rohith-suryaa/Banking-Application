package com.viiva.dao.branch;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import com.viiva.exceptions.DBException;
import com.viiva.pojo.branch.Branch;
import com.viiva.util.BasicUtil;
import com.viiva.util.DBUtil;

public class BranchDAO {

	private static final String BANK_CODE = "VIIV";
	private static final char RESERVED = '0';

	public Map<String, Object> addBranch(Branch branch) {

		String query = "INSERT INTO branch (manager_id, ifsc_code, locality, district, state, created_time) VALUES(?,?,?,?,?,?)";

		try (PreparedStatement pstmt = DBUtil.prepareWithKeys(DBUtil.getConnection(), query)) {

			pstmt.setObject(1, branch.getManagerId());
			pstmt.setString(2, "");
			pstmt.setString(3, branch.getLocality());
			pstmt.setString(4, branch.getDistrict());
			pstmt.setString(5, branch.getState());
			pstmt.setLong(6, System.currentTimeMillis());

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

								System.out.println("Completed Branch DAO with result");
								return result;

							}

							System.out.println("Updating the IFSC Failed");
							return null;
						}

					}
				}
			}
			System.out.println("Completed Branch DAO with null");
			return null;

		} catch (SQLException e) {
			e.printStackTrace();
			throw new DBException("Error occurred while registering a branch.", e);
		}
	}

	public Branch updateBranch(Branch branch) {
		String query = "UPDATE branch SET manager_id=?,locality=?,district=?,state=?,modified_time=?,modified_by=? WHERE branch_id=?";

		try (PreparedStatement pstmt = DBUtil.prepare(DBUtil.getConnection(), query)) {
			long now = System.currentTimeMillis();

			pstmt.setLong(1, branch.getManagerId());
			pstmt.setString(2, branch.getLocality());
			pstmt.setString(3, branch.getDistrict());
			pstmt.setString(4, branch.getState());
			pstmt.setLong(5, now);
			pstmt.setLong(6, branch.getModifiedBy());
			pstmt.setLong(7, branch.getBranchId());

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
		String query = "SELECT branch_id,manager_id,ifsc_code,locality,district,state,created_time,modified_time,modified_by FROM branch WHERE branch_id = ?";
		try (PreparedStatement pstmt = DBUtil.prepare(DBUtil.getConnection(), query)) {
			pstmt.setLong(1, branchId);

			try (ResultSet rs = DBUtil.executeQuery(pstmt)) {
				while (rs.next()) {

					Branch branch = new Branch();
					branch.setBranchId(rs.getLong("branch_id"));
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

	protected static String generateIFSC(long branchId) {
		if (!BasicUtil.isBlank(branchId)) {
			String branchCode = String.format("%06d", branchId);
			return BANK_CODE + RESERVED + branchCode;
		}
		return null;
	}

}
