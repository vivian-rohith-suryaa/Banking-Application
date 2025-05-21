package com.viiva.handler.employee;

import java.util.HashMap;
import java.util.Map;
import com.viiva.dao.employee.EmployeeDAO;
import com.viiva.dao.user.UserDAO;
import com.viiva.exceptions.DBException;
import com.viiva.exceptions.InputException;
import com.viiva.handler.Handler;
import com.viiva.pojo.employee.Employee;
import com.viiva.pojo.user.UserType;
import com.viiva.util.BasicUtil;
import com.viiva.util.DBUtil;

public class EmployeeHandler implements Handler<Employee> {

	@Override
	public Object handle(String methodAction, Employee data) throws Exception {

		switch (methodAction) {
		case "POST":
			try {
				if (!BasicUtil.isNull(data)) {
					long employee_id = data.getEmployeeId();
					UserDAO userDao = new UserDAO();
					Map<String, Object> employee = userDao.getEmployeeById(employee_id);

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
					
					boolean updated = userDao.updateToEmployee(employee_id);
					if(!updated) {
						throw new DBException("Failed to update user type to EMPLOYEE.");
					}
					EmployeeDAO empDao = new EmployeeDAO();
					Map<String, Object> result = empDao.addEmployee(data);
					
					if (BasicUtil.isNull(result)) {
						throw new DBException("Employee Registration Failed.");
					}
					
					DBUtil.commit();
					
					System.out.println("New Employee added:\nEmployee_Id: "+result.get("employeeId")+"\n Branch_Id: "+result.get("branchId"));
					
					Map<String, Object> responseData = new HashMap<String, Object>();
					responseData.put("message", "Success");
					responseData.put("employeeId", result.get("employeeId"));
					responseData.put("branchId", result.get("branchId"));
					
					return responseData;
					
				} else {
					throw new InputException("Null Input.");
				}
			} catch (Exception e) {
				DBUtil.rollback();
				throw (Exception) e;
			}
			
		case "GET":
			try {
				if (!BasicUtil.isNull(data)) {
					long employeeId = data.getEmployeeId();
					if (BasicUtil.isBlank(employeeId)) {
						throw new InputException("Null/Empty Employee Id");
					}
					
					EmployeeDAO empDao = new EmployeeDAO();
					Employee employee = empDao.getEmployeeById(employeeId);
					
					if(BasicUtil.isNull(employee)) {
						throw new DBException("Fetching Employee details failed.");
					}
					
					DBUtil.commit();

					Map<String, Object> responseData = new HashMap<String, Object>();

					responseData.put("message", "Success");
					responseData.put("employeeId", employee.getEmployeeId());
					responseData.put("name", employee.getName());
					responseData.put("email", employee.getEmail());
					responseData.put("phone", employee.getPhone());
					responseData.put("type", employee.getType());
					responseData.put("branchId", employee.getBranchId());
					
					return responseData;
				}
			} catch (Exception e) {
				DBUtil.rollback();
				throw (Exception) e;
			}
			
		case "PUT":
			try {
				if (!BasicUtil.isNull(data)) {
					EmployeeDAO empDao = new EmployeeDAO();
					Employee updatedEmployee = empDao.updateEmployee(data);
					
					if (BasicUtil.isNull(updatedEmployee)) {
						throw new DBException("Updating User details failed.");
					}
					
					DBUtil.commit();

					Map<String, Object> responseData = new HashMap<String, Object>();

					responseData.put("message", "Success");
					responseData.put("employeeId", updatedEmployee.getEmployeeId());
					responseData.put("name", updatedEmployee.getName());
					responseData.put("email", updatedEmployee.getEmail());
					responseData.put("phone", updatedEmployee.getPhone());
					responseData.put("type", updatedEmployee.getType());
					responseData.put("branchId", updatedEmployee.getBranchId());
					responseData.put("modifiedTime", updatedEmployee.getModifiedTime());
					responseData.put("modifiedBy", updatedEmployee.getModifiedBy());
					
					return responseData;
				} else {
					throw new InputException("Null Input.");
				}
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
