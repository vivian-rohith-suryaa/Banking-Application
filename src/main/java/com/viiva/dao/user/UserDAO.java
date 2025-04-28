package com.viiva.dao.user;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import com.viiva.exceptions.DBException;
import com.viiva.pojo.user.User;
import com.viiva.pojo.user.UserStatus;
import com.viiva.pojo.user.UserType;
import com.viiva.util.DBUtil;

public class UserDAO {

	public Map<String, Object> signupUser(User user) {
		String query = "INSERT INTO user (name, email, phone, gender, password, type, status, created_time, modified_time, modified_by) VALUES (?,?,?,?,?,?,?,?,?,?)";

		try (Connection conn = DBUtil.getConnection(); PreparedStatement pstmt = DBUtil.prepare(conn, query)) {

			pstmt.setString(1, user.getName());
			pstmt.setString(2, user.getEmail());
			pstmt.setString(3, user.getPhone());
			pstmt.setString(4, user.getGender().name());
			pstmt.setString(5, user.getPassword());
			pstmt.setByte(6, (byte) UserType.CUSTOMER.getCode());
			pstmt.setByte(7, (byte) UserStatus.ACTIVE.getCode());
			pstmt.setLong(8, System.currentTimeMillis());
			pstmt.setObject(9, null);
			pstmt.setObject(10, null);

			int rows = DBUtil.executeUpdate(pstmt);
			if (rows > 0) {
				try (ResultSet rs = pstmt.getGeneratedKeys()) {
					if (rs.next()) {
						long userId = rs.getLong(1);
						Map<String, Object> result = new HashMap<>();
						result.put("userId", userId);
						result.put("userType", UserType.CUSTOMER.getCode());

						return result;
					}
				}
			}
			return null;
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DBException("Error while signing up the user.", e);
		}
	}

	public String authUser(String email) {
		String query = "SELECT user_id,password,type FROM user WHERE email = ?";

		try (Connection conn = DBUtil.getConnection(); PreparedStatement pstmt = DBUtil.prepare(conn, query)) {

			pstmt.setString(1, email);

			try (ResultSet rs = DBUtil.executeQuery(pstmt)) {
				if (rs.next()) {
					return rs.getString("password");
				}
				return null;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DBException("Error occurred while fetching in the user credentials.", e);
		}

	}

	public Map<String, Object> getSessionDetail(String email) {
		String query = "SELECT user_id,type FROM user WHERE email = ?";

		try (Connection conn = DBUtil.getConnection(); PreparedStatement pstmt = DBUtil.prepare(conn, query)) {

			pstmt.setString(1, email);

			try (ResultSet rs = DBUtil.executeQuery(pstmt)) {
				if (rs.next()) {
					Map<String, Object> result = new HashMap<String, Object>();

					result.put("userId", rs.getString("user_id"));
					result.put("userType", rs.getByte("type"));

					return result;
				}
				return null;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DBException("Error occurred while fetching session details.");
		}
	}

}
