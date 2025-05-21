package com.viiva.dao.employee;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import com.viiva.exceptions.DBException;
import com.viiva.pojo.employee.Employee;
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

		String query = "SELECT e.employee_id,u.name,u.email,u.phone,u.type,e.branch_id,u.modified_time,u.modified_by FROM employee e JOIN user u on e.employee_id = u.user_id WHERE employee_id =?";

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

}
