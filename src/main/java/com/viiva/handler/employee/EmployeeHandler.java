package com.viiva.handler.employee;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.viiva.dao.employee.EmployeeDAO;
import com.viiva.dao.user.UserDAO;
import com.viiva.exceptions.AuthException;
import com.viiva.exceptions.DBException;
import com.viiva.exceptions.InputException;
import com.viiva.handler.Handler;
import com.viiva.pojo.employee.Employee;
import com.viiva.pojo.user.UserType;
import com.viiva.session.SessionAware;
import com.viiva.util.BasicUtil;
import com.viiva.util.DBUtil;

public class EmployeeHandler implements Handler<Employee> {

	@Override
	public Object handle(String methodAction, Employee data) throws Exception {

		if (!(data instanceof SessionAware)) {
			throw new AuthException("Unauthorized: Session not found.");
		}

		byte sessionRole = data.getSessionRole();
		long sessionUserId = data.getSessionUserId();
		long sessionBranchId = data.getSessionBranchId();

		switch (methodAction) {
		case "POST":
			try {
				if (sessionRole != 3 && sessionRole != 4) {
					throw new AuthException("Access Denied: Unauthorised to add an employee.");
				}
				if (BasicUtil.isNull(data)) {
					throw new InputException("Invalid (Null) Input.");
				}

				data.setBranchId(sessionBranchId);

				long employeeId = data.getEmployeeId();
				UserDAO userDao = new UserDAO();

				Map<String, Object> employee = userDao.getEmployeeById(employeeId);

				if (BasicUtil.isNull(employee)) {
					throw new DBException("Couldnt find the user for the given id.");
				}

				System.out.println("User found: " + employee.get("userId") + " Type: " + employee.get("type"));

				UserType type = (UserType) employee.get("type");
				if (type == UserType.EMPLOYEE) {
					throw new DBException("User is already an employee.");
				}

				if (type == UserType.MANAGER) {
					throw new DBException("User is already a Manager.");
				}

				boolean updated = userDao.updateToEmployee(employeeId, sessionUserId);
				if (!updated) {
					throw new DBException("Failed to update user type to EMPLOYEE.");
				}
				EmployeeDAO empDao = new EmployeeDAO();
				Map<String, Object> result = empDao.addEmployee(data);

				if (BasicUtil.isNull(result)) {
					throw new DBException("Employee Registration Failed.");
				}

				DBUtil.commit();

				System.out.println("New Employee added:\nEmployee_Id: " + result.get("employeeId") + "\n Branch_Id: "
						+ result.get("branchId"));

				Map<String, Object> responseData = new HashMap<String, Object>();
				responseData.put("message", "Employee Registered Successfully.");
				responseData.put("employeeId", result.get("employeeId"));
				responseData.put("branchId", result.get("branchId"));

				return responseData;

			} catch (Exception e) {
				DBUtil.rollback();
				throw (Exception) e;
			}

		case "GET":
			try {
				if (sessionRole != 2 && sessionRole != 3 && sessionRole != 4) {
					throw new AuthException("Access Denied: Unauthorised to view employee.");
				}
				if (BasicUtil.isNull(data)) {
					throw new InputException("Invalid (Null) Input.");
				}

				EmployeeDAO empDao = new EmployeeDAO();

				long employeeId = data.getEmployeeId();
				
				if (BasicUtil.isBlank(employeeId)) {
					throw new InputException("Null/Empty Employee Id.");
				}
				
				if (employeeId == 0) {
					List<Employee> employees = empDao.getBranchEmployees(sessionBranchId);

					if (employees.isEmpty() || employees == null) {
						throw new DBException("Employees Not found for this branch.");
					}

					List<Map<String, Object>> resultList = new ArrayList<>();
					for (Employee emp : employees) {
						Map<String, Object> empMap = new HashMap<>();
						empMap.put("employeeId", emp.getEmployeeId());
						empMap.put("name", emp.getName());
						empMap.put("email", emp.getEmail());
						empMap.put("phone", emp.getPhone());
						empMap.put("type", emp.getType());
						empMap.put("branchId", emp.getBranchId());
						resultList.add(empMap);
					}

					DBUtil.commit();

					Map<String, Object> response = new HashMap<>();
					response.put("message", "Employees Fetched Successfully.");
					response.put("employees", resultList);
					return response;
				} else {

					Employee employee = empDao.getEmployeeById(employeeId);

					if (BasicUtil.isNull(employee)) {
						throw new DBException("Employee not found.");
					}

					DBUtil.commit();

					Map<String, Object> responseData = new HashMap<String, Object>();

					responseData.put("message", "Employee Detail fetched successfully.");
					responseData.put("employeeId", employee.getEmployeeId());
					responseData.put("name", employee.getName());
					responseData.put("email", employee.getEmail());
					responseData.put("phone", employee.getPhone());
					responseData.put("type", employee.getType());
					responseData.put("branchId", employee.getBranchId());
					responseData.put("modifiedBy", employee.getModifiedBy());
					responseData.put("modifiedTime", employee.getModifiedTime());

					return responseData;
				}
			} catch (Exception e) {
				DBUtil.rollback();
				throw (Exception) e;
			}

		case "PUT":
			try {
				if (sessionRole != 3 && sessionRole != 4) {
					throw new AuthException("Access Denied: Unauthorised to update an employee.");
				}

				if (sessionRole != 4) {
					if (data.getBranchId() != data.getSessionBranchId()) {
						throw new AuthException("Access Denied: Unauthorised to update other branch details.");
					}
				}

				if (BasicUtil.isNull(data)) {
					throw new InputException("Invalid (Null) Input.");
				}
				data.setModifiedBy(sessionUserId);
				EmployeeDAO empDao = new EmployeeDAO();
				Employee updatedEmployee = empDao.updateEmployee(data);

				if (BasicUtil.isNull(updatedEmployee)) {
					throw new DBException("Employee not found.");
				}

				DBUtil.commit();

				Map<String, Object> responseData = new HashMap<String, Object>();

				responseData.put("message", "Employee Updated Successfully.");
				responseData.put("employeeId", updatedEmployee.getEmployeeId());
				responseData.put("name", updatedEmployee.getName());
				responseData.put("email", updatedEmployee.getEmail());
				responseData.put("phone", updatedEmployee.getPhone());
				responseData.put("type", updatedEmployee.getType());
				responseData.put("branchId", updatedEmployee.getBranchId());
				responseData.put("modifiedTime", updatedEmployee.getModifiedTime());
				responseData.put("modifiedBy", updatedEmployee.getModifiedBy());

				return responseData;
			} catch (Exception e) {
				DBUtil.rollback();
				throw (Exception) e;
			}

		default:
			throw new Exception("Invalid Method Action: " + methodAction);
		}
	}

	@Override
	public Class<Employee> getRequestType() {
		return Employee.class;
	}

}
