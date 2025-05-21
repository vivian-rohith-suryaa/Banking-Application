package com.viiva.dao.user;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.HashMap;
import java.util.Map;
import com.viiva.exceptions.DBException;
import com.viiva.pojo.user.Gender;
import com.viiva.pojo.user.User;
import com.viiva.pojo.user.UserStatus;
import com.viiva.pojo.user.UserType;
import com.viiva.util.DBUtil;

public class UserDAO {

	public Map<String, Object> signupUser(User user) {

		String query = "INSERT INTO user (name, email, phone, gender, password, type, status, created_time, modified_time, modified_by) VALUES (?,?,?,?,?,?,?,?,?,?)";

		try (PreparedStatement pstmt = DBUtil.prepareWithKeys(DBUtil.getConnection(), query)) {

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
						result.put("userType", (byte) UserType.CUSTOMER.getCode());

						return result;
					}
				}
			}
			return null;
		} catch (SQLIntegrityConstraintViolationException e) {

			String message = e.getMessage();

			if (message.contains("'user.email'")) {
				throw new DBException("Duplicate Entry for Email found.");
			} else if (message.contains("user.phone")) {
				throw new DBException("Duplicate Entry for Phone found.");
			} else {
				throw new DBException("Constraint Violation occurred Database.");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DBException("Error while signing up the user.", e);
		}
	}

	public Map<String, Object> authenticate(String email) {

		String query = "SELECT user_id,password,type FROM user WHERE email = ?";

		try (PreparedStatement pstmt = DBUtil.prepare(DBUtil.getConnection(), query)) {

			pstmt.setString(1, email);

			try (ResultSet rs = DBUtil.executeQuery(pstmt)) {
				if (rs.next()) {
					Map<String, Object> result = new HashMap<String, Object>();

					result.put("userId", rs.getString("user_id"));
					result.put("userType", rs.getByte("type"));
					result.put("password", rs.getString("password"));

					return result;
				}
				return null;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DBException("Error occurred while fetching the user credentials.", e);
		}

	}

	public User getUserById(long userId) {
		String query = "SELECT user_id, name, email, phone, gender,type, status FROM user WHERE user_id = ?";

		try (PreparedStatement pstmt = DBUtil.prepare(DBUtil.getConnection(), query)) {
			pstmt.setLong(1, userId);

			try (ResultSet rs = DBUtil.executeQuery(pstmt)) {
				while (rs.next()) {
					User user = new User();
					user.setUserId(rs.getLong("user_id"));
					user.setName(rs.getString("name"));
					user.setEmail(rs.getString("email"));
					user.setPhone(rs.getString("phone"));
					user.setGender(Gender.fromString(rs.getString("gender")));
					user.setType(UserType.fromCode(rs.getByte("type")));
					user.setStatus(UserStatus.fromCode(rs.getByte("status")));
					return user;
				}
				return null;
			}

		} catch (SQLException e) {
			e.printStackTrace();
			throw new DBException("Error occurred while fetching the user details.", e);
		}

	}

	public User updateUser(User user) {

		String query = "UPDATE user SET name=?,email=?,phone=?,gender=?,modified_time=?,modified_by=? WHERE user_id=?";
		
		try (PreparedStatement pstmt = DBUtil.prepare(DBUtil.getConnection(), query)) {
			long now = System.currentTimeMillis();
			pstmt.setString(1, user.getName());
			pstmt.setString(2, user.getEmail());
			pstmt.setString(3, user.getPhone());
			pstmt.setString(4, user.getGender().name());
			pstmt.setLong(5, now);
			pstmt.setLong(6, user.getUserId());
			pstmt.setLong(7, user.getUserId());

			int rows = DBUtil.executeUpdate(pstmt);
			if (rows > 0) {
				user.setModifiedBy(user.getUserId());
				user.setModifiedTime(user.getModifiedTime());
				return user;
			}
			return null;

		} catch (SQLIntegrityConstraintViolationException e) {

			String message = e.getMessage();

			if (message.contains("'user.email'")) {
				throw new DBException("Duplicate Entry for Email found.");
			} else if (message.contains("user.phone")) {
				throw new DBException("Duplicate Entry for Phone found.");
			} else {
				throw new DBException("Constraint Violation occurred Database.");
			}

		} catch (SQLException e) {
			e.printStackTrace();
			throw new DBException("Error occurred while updating the user details.", e);
		}
	}
	
	public Map<String,Object> getEmployeeById(long employee_id) {
		String query = "SELECT user_id,type FROM user WHERE user_id = ?";

		try (PreparedStatement pstmt = DBUtil.prepare(DBUtil.getConnection(), query)) {
			pstmt.setLong(1, employee_id);

			try (ResultSet rs = DBUtil.executeQuery(pstmt)) {
				while (rs.next()) {
					Map<String, Object> employeeMap = new HashMap<String, Object>();
					employeeMap.put("userId", rs.getLong("user_id"));
					employeeMap.put("type", UserType.fromCode(rs.getByte("type")));
					return employeeMap;
				}
				return null;
			}

		} catch (SQLException e) {
			e.printStackTrace();
			throw new DBException("Error occurred while fetching the user by id.", e);
		}

	}
	
	public boolean updateToEmployee(long employee_id) {
		String query = "UPDATE user SET type=?,modified_time=?,modified_by=? WHERE user_id=?";
		
		try (PreparedStatement pstmt = DBUtil.prepare(DBUtil.getConnection(), query)) {
			pstmt.setByte(1, (byte)UserType.EMPLOYEE.getCode());
			pstmt.setLong(2, System.currentTimeMillis());
			pstmt.setLong(3, 0);
			pstmt.setLong(4, employee_id);
	
			int rows = DBUtil.executeUpdate(pstmt);
			return rows>0;
			
		}  catch (SQLException e) {
			e.printStackTrace();
			throw new DBException("Error occurred while updating the user details.", e);
		}
	}

}
