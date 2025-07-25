package com.viiva.handler.employee;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.viiva.dao.branch.BranchDAO;
import com.viiva.dao.employee.EmployeeDAO;
import com.viiva.dao.user.UserDAO;
import com.viiva.exceptions.AuthException;
import com.viiva.exceptions.DBException;
import com.viiva.exceptions.InputException;
import com.viiva.handler.Handler;
import com.viiva.pojo.branch.Branch;
import com.viiva.pojo.employee.Employee;
import com.viiva.pojo.user.UserStatus;
import com.viiva.pojo.user.UserType;
import com.viiva.session.SessionAware;
import com.viiva.util.BasicUtil;
import com.viiva.util.DBUtil;
import com.viiva.util.InputValidator;

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
					throw new AuthException("Access Denied: Unauthorized to add an employee.");
				}

				if (BasicUtil.isNull(data)) {
					throw new InputException("Invalid (null) input data.");
				}
				
				UserType requestedType = data.getType();
				if (requestedType == null) {
					requestedType = UserType.EMPLOYEE;
				}

				if (sessionRole == 3 && requestedType != UserType.EMPLOYEE) {
					throw new AuthException("Access Denied: Unauthorised Action.");
				}
				
				if (sessionRole == 4 && (requestedType != UserType.EMPLOYEE && requestedType != UserType.MANAGER)) {
					throw new InputException("Access Denied: Unauthorised Action.");
				}
				
				if (sessionRole == 3) {
					data.setBranchId(sessionBranchId);
				} else if (sessionRole == 4) {
					if (BasicUtil.isNull(data.getBranchId()) || data.getBranchId() <= 0) {
						throw new InputException("Invalid Branch ID.");
					}
				}
				
				data.setType(requestedType);
				data.setStatus(UserStatus.ACTIVE);
				data.setCreatedTime(System.currentTimeMillis());
				data.setModifiedTime(System.currentTimeMillis());
				data.setModifiedBy(sessionUserId);
				
				StringBuilder validationResult = InputValidator.validateEmployee(data);

				if (!InputValidator.isStrongPassword(data.getPassword())) {
					validationResult.append("Password: " + data.getPassword()
							+ ". The password must be 8 to 20 characters long, include at least one uppercase letter, at least one number, and at least one special character (@$!%*?&#).");
				}

				if (!validationResult.toString().isEmpty()) {
					throw new InputException("Invalid Input(s) found: " + validationResult);
				}
				
				data.setPassword(BasicUtil.encrypt(data.getPassword()));
			
				UserDAO userDao = new UserDAO();
				Map<String, Object> userResult = userDao.createEmployee(data);
				if (BasicUtil.isNull(userResult)) {
					throw new DBException("Failed to create User.");
				}
				
				long userId = (long) userResult.get("userId");
				data.setEmployeeId(userId);
				
				EmployeeDAO empDao = new EmployeeDAO();
				Map<String, Object> result = empDao.addEmployee(data);
				if (BasicUtil.isNull(result)) {
					throw new DBException("Failed to register employee.");
				}

				DBUtil.commit();

				Map<String, Object> response = new HashMap<>();
				response.put("message", "Employee created successfully.");
				response.put("employeeId", result.get("employeeId"));
				response.put("branchId", result.get("branchId"));
				response.put("role", data.getType().name());

				return response;

			} catch (Exception e) {
				DBUtil.rollback();
				throw e;
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

				if (BasicUtil.isBlank(data.getEmployeeId())) {
					return getAllEmployees(data);
				}
				long employeeId = data.getEmployeeId();
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

				if (updatedEmployee.getType() == UserType.MANAGER) {
					BranchDAO branchDao = new BranchDAO();
					Branch branch = branchDao.getBranchById(updatedEmployee.getBranchId());
					if (BasicUtil.isNull(branch)) {
						throw new DBException("Branch not found.");
					}
					branch.setManagerId(updatedEmployee.getEmployeeId());
					branch.setModifiedBy(sessionUserId);
					Branch updatedBranch = branchDao.updateBranch(branch);

					if (BasicUtil.isNull(updatedBranch)) {
						throw new DBException("Failed to update branch with new manager.");
					}
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

	private Object getAllEmployees(Employee data) throws Exception {
		Map<String, Object> session = data.getSessionAttributes();
		byte role = Byte.parseByte(session.get("role").toString());

		Long branchId = session.containsKey("branchId") ? Long.parseLong(session.get("branchId").toString()) : null;
		Map<String, String> queryParams = data.getQueryParams();

		EmployeeDAO empDao = new EmployeeDAO();
		List<Employee> employees = empDao.getAllEmployees(role, branchId, queryParams);

		if (employees.isEmpty() || employees == null) {
			throw new DBException("Employees Not Found.");
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

	}

}
