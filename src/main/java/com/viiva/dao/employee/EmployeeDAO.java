package com.viiva.dao.employee;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.viiva.exceptions.DBException;
import com.viiva.pojo.employee.Employee;
import com.viiva.pojo.user.Gender;
import com.viiva.pojo.user.UserStatus;
import com.viiva.pojo.user.UserType;
import com.viiva.util.DBUtil;

public class EmployeeDAO {

	public Map<String, Object> addEmployee(Employee employee) {
		String query = "INSERT INTO employee(employee_id,branch_id) VALUES (?,?)";

		try (PreparedStatement pstmt = DBUtil.prepare(DBUtil.getConnection(), query)) {

			pstmt.setLong(1, employee.getEmployeeId());
			pstmt.setLong(2, employee.getBranchId());

			int rows = DBUtil.executeUpdate(pstmt);
			if (rows > 0) {
				Map<String, Object> result = new HashMap<>();
				result.put("employeeId", employee.getEmployeeId());
				result.put("branchId", employee.getBranchId());

				return result;
			}
			return null;
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DBException("Error occurred while adding employee", e);
		}
	}

	public Employee getEmployeeById(long empId) {

		String query = "SELECT e.employee_id,u.name,u.email,u.phone,u.type,e.branch_id,u.modified_time,u.modified_by FROM employee e JOIN user u on e.employee_id = u.user_id WHERE e.employee_id =?";

		try (PreparedStatement pstmt = DBUtil.prepare(DBUtil.getConnection(), query)) {

			pstmt.setLong(1, empId);

			try (ResultSet rs = DBUtil.executeQuery(pstmt)) {
				while (rs.next()) {
					Employee employee = new Employee();
					employee.setEmployeeId(rs.getLong("employee_id"));
					employee.setName(rs.getString("name"));
					employee.setEmail(rs.getString("email"));
					employee.setPhone(rs.getString("phone"));
					employee.setType(UserType.fromCode(rs.getByte("type")));
					employee.setBranchId(rs.getLong("branch_id"));
					employee.setModifiedTime(rs.getLong("modified_time"));
	                employee.setModifiedBy(rs.getLong("modified_by")); 

					return employee;
				}
				return null;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DBException("Error occurred while fetching employee", e);
		}

	}

	public Employee updateEmployee(Employee employee) {
		String query = "UPDATE employee e JOIN user u ON e.employee_id = u.user_id SET e.branch_id =?,u.type =?,u.modified_time=?,u.modified_by=? WHERE e.employee_id = ?";

		try (PreparedStatement pstmt = DBUtil.prepare(DBUtil.getConnection(), query)) {
			long now = System.currentTimeMillis();
			pstmt.setLong(1, employee.getBranchId());
			pstmt.setByte(2, employee.getType().getCode());
			pstmt.setLong(3, now);
			pstmt.setLong(4, employee.getModifiedBy());
			pstmt.setLong(5, employee.getEmployeeId());

			int rows = DBUtil.executeUpdate(pstmt);
			if (rows > 0) {
				Employee updatedEmployee = getEmployeeById(employee.getEmployeeId());
				return updatedEmployee;
			}
			return null;

		} catch (SQLException e) {
			e.printStackTrace();
			throw new DBException("Error occurred while updating employee", e);
		}
	}

	public List<Employee> getAllEmployees(byte role, Long branchId, Map<String, String> filters) throws Exception {
		
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
	    
	    List<Employee> employees = new ArrayList<>();

	    StringBuilder query = new StringBuilder(
	        "SELECT e.employee_id, u.name, u.email, u.phone, u.gender, u.type, u.status, u.modified_by, u.modified_time, e.branch_id " +
	        "FROM employee e INNER JOIN user u ON e.employee_id = u.user_id"
	    );

	    if (role == 2 || role == 3) {
	        query.append(" WHERE e.branch_id = ?");
	    } else {
	        query.append(" WHERE 1=1");
	    }

	    if (filters != null) {
	        if (filters.containsKey("type")) {
	            query.append(" AND u.type = ?");
	        }
	        if (filters.containsKey("status")) {
	            query.append(" AND u.status = ?");
	        }
	        if (filters.containsKey("branchId") && role == 4) {
	            query.append(" AND e.branch_id = ?");
	        }
	        if (filters.containsKey("employeeId")) {
	            query.append(" AND e.employee_id = ?");
	        }
	    }
	    query.append(" ORDER BY e.employee_id DESC LIMIT ? OFFSET ?");

	    try (PreparedStatement pstmt = DBUtil.prepare(DBUtil.getConnection(), query.toString())) {
	        int index = 1;

	        if (role == 2 || role == 3) {
	            pstmt.setLong(index++, branchId);
	        }

	        if (filters != null) {
	            if (filters.containsKey("type")) {
	                pstmt.setByte(index++, Byte.parseByte(filters.get("type")));
	            }
	            if (filters.containsKey("status")) {
	                pstmt.setByte(index++, Byte.parseByte(filters.get("status")));
	            }
	            if (filters.containsKey("branchId") && role == 4) {
	                pstmt.setLong(index++, Long.parseLong(filters.get("branchId")));
	            }
	            if (filters.containsKey("employeeId")) {
	                pstmt.setLong(index++, Long.parseLong(filters.get("employeeId")));
	            }
	        }
	        
	        pstmt.setInt(index++, limit);
	        pstmt.setInt(index, offset);

	        try (ResultSet rs = DBUtil.executeQuery(pstmt)) {
	            while (rs.next()) {
	                Employee emp = new Employee();
	                emp.setEmployeeId(rs.getLong("employee_id"));
	                emp.setName(rs.getString("name"));
	                emp.setEmail(rs.getString("email"));
	                emp.setPhone(rs.getString("phone"));
	                emp.setGender(Gender.fromString(rs.getString("gender")));
	                emp.setType(UserType.fromCode(rs.getByte("type")));
	                emp.setStatus(UserStatus.fromCode(rs.getByte("status")));
	                emp.setModifiedBy(rs.getLong("modified_by"));
	                emp.setModifiedTime(rs.getLong("modified_time"));
	                emp.setBranchId(rs.getLong("branch_id"));
	                employees.add(emp);
	            }
	        }

	        return employees;
	    } catch (SQLException e) {
	        e.printStackTrace();
	        throw new DBException("Error occurred while fetching employees.", e);
	    }
	}


}
