package com.viiva.dao.user;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.viiva.exceptions.AuthException;
import com.viiva.exceptions.DBException;
import com.viiva.pojo.employee.Employee;
import com.viiva.pojo.user.Gender;
import com.viiva.pojo.user.User;
import com.viiva.pojo.user.UserStatus;
import com.viiva.pojo.user.UserType;
import com.viiva.util.DBUtil;

public class UserDAO {

	public Map<String, Object> signupUser(User user) {

		String query = "INSERT INTO user (name, email, phone, gender, password, type, status, created_time) VALUES (?,?,?,?,?,?,?,?)";

		try (PreparedStatement pstmt = DBUtil.prepareWithKeys(DBUtil.getConnection(), query)) {

			pstmt.setString(1, user.getName());
			pstmt.setString(2, user.getEmail());
			pstmt.setString(3, user.getPhone());
			pstmt.setString(4, user.getGender().name());
			pstmt.setString(5, user.getPassword());
			pstmt.setByte(6, (byte) UserType.CUSTOMER.getCode());
			pstmt.setByte(7, (byte) UserStatus.ACTIVE.getCode());
			pstmt.setLong(8, System.currentTimeMillis());

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
				throw new DBException("Duplicate Email Address.");
			} else if (message.contains("user.phone")) {
				throw new DBException("Duplicate Phone Number.");
			} else {
				throw new DBException("Constraint Violation occurred Database.");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DBException("Error while signing up the user.", e);
		}
	}
	
	public Map<String, Object> createEmployee(Employee employee) {

		String query = "INSERT INTO user (name, email, phone, gender, password, type, status, created_time,modified_time,modified_by) VALUES (?,?,?,?,?,?,?,?,?,?)";

		try (PreparedStatement pstmt = DBUtil.prepareWithKeys(DBUtil.getConnection(), query)) {

			pstmt.setString(1, employee.getName());
			pstmt.setString(2, employee.getEmail());
			pstmt.setString(3, employee.getPhone());
			pstmt.setString(4, employee.getGender().name());
			pstmt.setString(5, employee.getPassword());
			pstmt.setByte(6,employee.getType().getCode());
			pstmt.setByte(7, employee.getStatus().getCode());
			pstmt.setLong(8, System.currentTimeMillis());
			pstmt.setLong(9, System.currentTimeMillis());
			pstmt.setLong(10, employee.getModifiedBy());

			int rows = DBUtil.executeUpdate(pstmt);
			if (rows > 0) {
				try (ResultSet rs = pstmt.getGeneratedKeys()) {
					if (rs.next()) {
						long userId = rs.getLong(1);
						Map<String, Object> result = new HashMap<>();
						result.put("userId", userId);
						result.put("userType", employee.getType().getCode());

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
	
	public String getUserPassword(long userId) {
		
		String query = "SELECT password FROM user WHERE user_id = ?";
		
		try (PreparedStatement pstmt = DBUtil.prepare(DBUtil.getConnection(), query)) {
			pstmt.setLong(1, userId);
			
			try (ResultSet rs = DBUtil.executeQuery(pstmt)) {
				if (rs.next()) {
					return rs.getString("password");
				}
				return null;
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DBException("Error occurred while fetching the user credentials.", e);
		}
	}


	public Map<String, Object> authenticate(String email) {
		String query = "SELECT user_id, password, type, "
				+ "(SELECT branch_id FROM employee WHERE employee_id = user.user_id) AS branch_id "
				+ "FROM user WHERE email = ?";

		try (PreparedStatement pstmt = DBUtil.prepare(DBUtil.getConnection(), query)) {
			pstmt.setString(1, email);

			try (ResultSet rs = DBUtil.executeQuery(pstmt)) {
				if (rs.next()) {
					Map<String, Object> result = new HashMap<>();
					result.put("userId", rs.getLong("user_id"));
					result.put("userType", rs.getByte("type"));
					result.put("password", rs.getString("password"));

					byte role = rs.getByte("type");
					if (role == 2 || role == 3 || role == 4) {
						long branchId = rs.getLong("branch_id");
						if (!rs.wasNull()) {
							result.put("branchId", branchId);
						}
					}

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
		String query = "SELECT user_id, name, email, phone, gender,type, status, modified_by, modified_time FROM user WHERE user_id = ?";

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
					user.setModifiedBy(rs.getLong("modified_by"));
					user.setModifiedTime(rs.getLong("modified_time"));
					return user;
				}
				return null;
			}

		} catch (SQLException e) {
			e.printStackTrace();
			throw new DBException("Error occurred while fetching the user details.", e);
		}

	}

	public UserStatus getUserStatus(String email) throws AuthException {
		String query = "SELECT status FROM user WHERE email = ?";
		try (PreparedStatement pstmt = DBUtil.prepare(DBUtil.getConnection(), query)) {
			pstmt.setString(1, email);

			try (ResultSet rs = DBUtil.executeQuery(pstmt)) {
				if (rs.next()) {
					byte code = rs.getByte("status");
					return UserStatus.fromCode(code);
				} else {
					throw new AuthException("Invalid email. No user found.");
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DBException("Error occurred while fetching the user status.", e);
		}
	}
	
	public UserStatus getUserStatus(long userId) {
		String query = "SELECT status FROM user WHERE userId = ?";
		try (PreparedStatement pstmt = DBUtil.prepare(DBUtil.getConnection(), query)) {
			pstmt.setLong(1, userId);

			try (ResultSet rs = DBUtil.executeQuery(pstmt)) {
				if (rs.next()) {
					byte code = rs.getByte("status");
					return UserStatus.fromCode(code);
				} else {
					throw new DBException("User with ID " + userId + " not found.");
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DBException("Error occurred while fetching the user status.", e);
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
			pstmt.setLong(6, user.getModifiedBy());
			pstmt.setLong(7, user.getUserId());

			int rows = DBUtil.executeUpdate(pstmt);
			System.out.println(rows);
			if (rows > 0) {
				user.setModifiedBy(user.getUserId());
				user.setModifiedTime(System.currentTimeMillis());
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

	public Map<String, Object> getEmployeeById(long employee_id) {
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

	public boolean updateToEmployee(long employeeId, long modifiedBy) {
		String query = "UPDATE user SET type=?,modified_time=?,modified_by=? WHERE user_id=?";

		try (PreparedStatement pstmt = DBUtil.prepare(DBUtil.getConnection(), query)) {
			pstmt.setByte(1, (byte) UserType.EMPLOYEE.getCode());
			pstmt.setLong(2, System.currentTimeMillis());
			pstmt.setLong(3, modifiedBy);
			pstmt.setLong(4, employeeId);

			int rows = DBUtil.executeUpdate(pstmt);
			return rows > 0;

		} catch (SQLException e) {
			e.printStackTrace();
			throw new DBException("Error occurred while updating the user details.", e);
		}
	}

	public List<User> getAllUsers(byte role, Long branchId, Map<String, String> filters) throws Exception {
		List<User> users = new ArrayList<>();

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
				"SELECT DISTINCT u.user_id, u.name, u.email, u.phone, u.gender, u.type, u.status, u.modified_by, u.modified_time "
						+ "FROM user u");

		if (role == 2 || role == 3) {
			query.append(" INNER JOIN account a ON u.user_id = a.customer_id WHERE a.branch_id = ?");
		} else {
			query.append(" WHERE 1=1");
		}

		if (filters != null) {
			if (filters.containsKey("type")) {
				query.append(" AND u.type = ?");
			}
			if (filters.containsKey("userId")) {
				query.append(" AND u.user_id = ?");
			}
		}

		query.append(" ORDER BY u.user_id DESC LIMIT ? OFFSET ?");

		try (PreparedStatement pstmt = DBUtil.prepare(DBUtil.getConnection(), query.toString())) {
			int index = 1;

			if (role == 2 || role == 3) {
				pstmt.setLong(index++, branchId);
			}

			if (filters != null) {
				if (filters.containsKey("type")) {
					pstmt.setString(index++, filters.get("type"));
				}
				if (filters.containsKey("userId")) {
					pstmt.setString(index++, filters.get("userId"));
				}
			}

			pstmt.setInt(index++, limit);
			pstmt.setInt(index, offset);

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
					user.setModifiedBy(rs.getLong("modified_by"));
					user.setModifiedTime(rs.getLong("modified_time"));
					users.add(user);
				}
			}

			return users;
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DBException("Error occurred while fetching users.", e);
		}
	}

}
