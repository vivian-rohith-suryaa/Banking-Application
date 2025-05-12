package com.viiva.handler.user;

import java.util.HashMap;
import java.util.Map;
import com.viiva.dao.customer.CustomerDAO;
import com.viiva.dao.user.UserDAO;
import com.viiva.exceptions.DBException;
import com.viiva.exceptions.InputException;
import com.viiva.handler.Handler;
import com.viiva.pojo.customer.Customer;
import com.viiva.pojo.user.User;
import com.viiva.util.BasicUtil;
import com.viiva.util.DBUtil;
import com.viiva.util.InputValidator;
import com.viiva.wrapper.user.UserWrapper;

public class UserHandler implements Handler<UserWrapper> {

	@Override
	public Object handle(String methodAction, UserWrapper data) throws Exception {

		switch (methodAction) {

		case "GET":
			try {

				if (!BasicUtil.isNull(data)) {
					long userId = data.getUser().getUserId();
					if (BasicUtil.isBlank(userId)) {
						throw new InputException("Null/Empty User Id");
					}
					UserDAO userDao = new UserDAO();
					User user = userDao.getUserById(userId);

					if (BasicUtil.isNull(user)) {
						throw new DBException("Fetching User details failed.");
					}

					CustomerDAO customerDao = new CustomerDAO();

					Customer customer = customerDao.getCustomerById(userId);

					if (BasicUtil.isNull(customer)) {
						throw new DBException("Fetching User details failed.");
					}

					DBUtil.commit();

					Map<String, Object> responseData = new HashMap<String, Object>();

					responseData.put("message", "Success");
					responseData.put("userId", user.getUserId());
					responseData.put("name", user.getName());
					responseData.put("email", user.getEmail());
					responseData.put("phone", user.getPhone());
					responseData.put("gender", user.getGender());
					responseData.put("dob", customer.getDob());
					responseData.put("aadhar", customer.getAadhar());
					responseData.put("pan", customer.getPan());
					responseData.put("address", customer.getAddress());
					responseData.put("status", user.getStatus());

					return responseData;
				} else {
					throw new InputException("Null Input.");
				}
			} catch (Exception e) {
				DBUtil.rollback();
				throw (Exception) e;
			}

		case "PUT":

			try {
				if (!BasicUtil.isNull(data)) {

					String validationResult = InputValidator.validateUser(data).toString();

					if (!validationResult.isEmpty()) {
						throw new InputException("Invalid Input(s) found: " + validationResult);
					}

					User user = data.getUser();
					UserDAO userDao = new UserDAO();

					User updatedUser = userDao.updateUser(user);
					if (BasicUtil.isNull(updatedUser)) {
						throw new DBException("Updating User details failed.");
					}

					Customer customer = data.getCustomer();
					customer.setCustomerId(user.getUserId());
					CustomerDAO customerDao = new CustomerDAO();

					Customer updatedCustomer = customerDao.updateCustomer(customer);

					if (BasicUtil.isNull(updatedCustomer)) {
						throw new DBException("Updating User details failed.");
					}

					DBUtil.commit();

					Map<String, Object> responseData = new HashMap<String, Object>();

					responseData.put("message", "Success");
					responseData.put("userId", updatedUser.getUserId());
					responseData.put("name", updatedUser.getName());
					responseData.put("email", updatedUser.getEmail());
					responseData.put("phone", updatedUser.getPhone());
					responseData.put("gender", updatedUser.getGender());
					responseData.put("dob", updatedCustomer.getDob());
					responseData.put("aadhar", updatedCustomer.getDob());
					responseData.put("pan", updatedCustomer.getDob());
					responseData.put("address", updatedCustomer.getDob());
					responseData.put("status", updatedUser.getStatus());
					responseData.put("modified_time", updatedUser.getModifiedTime());
					responseData.put("modified_by", updatedUser.getModifiedBy());

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
	public Class<UserWrapper> getRequestType() {
		return UserWrapper.class;
	}

}
