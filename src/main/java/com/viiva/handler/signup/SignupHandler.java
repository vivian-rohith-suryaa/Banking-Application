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
import com.viiva.wrapper.signup.SignupRequest;

public class SignupHandler implements Handler<SignupRequest> {

	public Object handle(String methodAction, SignupRequest data) throws Exception {

		switch (methodAction) {
		case "POST":

			try {
				String validationResult = InputValidator.validateUser(data);

				if (!validationResult.isEmpty()) {
					throw new InputException("Invalid Input(s) found: " + validationResult);
				}

				User user = data.getUser();
				user.setPassword(BasicUtil.encrypt(user.getPassword()));

				DBUtil.start();
				UserDAO userDao = new UserDAO();
				Map<String, Object> userResult = userDao.signupUser(user);

				if (!BasicUtil.isNull(userResult)) {
					long userId = (long) userResult.get("userId");
					byte userType = (byte) userResult.get("userType");

					Customer customer = data.getCustomer();
					customer.setCustomerId(userId);

					CustomerDAO customerDao = new CustomerDAO();

					if (customerDao.signupCustomer(customer)) {

						DBUtil.commit();

						System.out.println("New user signedup\n" + "User Id: " + userId + "\nEmail:" + user.getEmail());

						Map<String, Object> responseData = new HashMap<>();
						responseData.put("message", "Signup Successful");
						responseData.put("userId", userId);
						responseData.put("userType", userType);

						return responseData;
					} else {
						DBUtil.rollback();
						throw new DBException("Customer Registration Failed.");
					}

				} else {
					DBUtil.rollback();
					throw new DBException("User Registration Failed.");

				}
			} catch (Exception e) {
				throw (Exception) e;
			}
			
		default:
			throw new Exception("Invalid Method Action: "+methodAction);
		}

	}

	@Override
	public Class<SignupRequest> getRequestType() {
		return SignupRequest.class;
	}

}