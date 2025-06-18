package com.viiva.handler.signup;

import java.util.HashMap;
import java.util.Map;
import com.viiva.exceptions.DBException;
import com.viiva.exceptions.InputException;
import com.viiva.dao.customer.CustomerDAO;
import com.viiva.dao.user.UserDAO;
import com.viiva.handler.Handler;
import com.viiva.pojo.customer.Customer;
import com.viiva.pojo.user.User;
import com.viiva.util.BasicUtil;
import com.viiva.util.DBUtil;
import com.viiva.util.InputValidator;
import com.viiva.wrapper.user.UserWrapper;

public class SignupHandler implements Handler<UserWrapper> {

	public Object handle(String methodAction, UserWrapper data) throws Exception {

		switch (methodAction) {
		case "POST":
			try {
				if (BasicUtil.isNull(data) || BasicUtil.isNull(data.getUser()) || BasicUtil.isNull(data.getCustomer())) {
					throw new InputException("Invalid (Null) Input.");
				}
				StringBuilder validationResult = InputValidator.validateUser(data);

				if (!InputValidator.isStrongPassword(data.getUser().getPassword())) {
					validationResult.append("Password: " + data.getUser().getPassword()
							+ ". The password must be 8 to 20 characters long, include at least one uppercase letter, at least one number, and at least one special character (@$!%*?&#).");
				}

				if (!validationResult.toString().isEmpty()) {
					throw new InputException("Invalid Input(s) found: " + validationResult);
				}

				User user = data.getUser();
				user.setPassword(BasicUtil.encrypt(user.getPassword()));

				UserDAO userDao = new UserDAO();
				Map<String, Object> userResult = userDao.signupUser(user);

				if (BasicUtil.isNull(userResult)) {
					throw new DBException("User Registration Failed.");
				}

				long userId = (long) userResult.get("userId");
				Byte userType = (Byte) userResult.get("userType");

				Customer customer = data.getCustomer();
				customer.setCustomerId(userId);

				CustomerDAO customerDao = new CustomerDAO();

				if (!customerDao.signupCustomer(customer)) {
					throw new DBException("User Registration Failed.");
				}

				DBUtil.commit();

				System.out.println("New user signedup\n" + "User Id: " + userId);

				Map<String, Object> responseData = new HashMap<>();
				responseData.put("message", "Signup Successful");
				responseData.put("userId", userId);
				responseData.put("role", userType);

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
	public Class<UserWrapper> getRequestType() {
		return UserWrapper.class;
	}

}