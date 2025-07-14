package com.viiva.handler.signup;

import java.util.HashMap;
import java.util.Map;

import com.viiva.dao.customer.CustomerDAO;
import com.viiva.dao.request.RequestDAO;
import com.viiva.dao.user.UserDAO;
import com.viiva.exceptions.DBException;
import com.viiva.exceptions.InputException;
import com.viiva.handler.Handler;
import com.viiva.pojo.account.AccountType;
import com.viiva.pojo.customer.Customer;
import com.viiva.pojo.request.Request;
import com.viiva.pojo.user.User;
import com.viiva.util.BasicUtil;
import com.viiva.util.DBUtil;
import com.viiva.util.InputValidator;
import com.viiva.wrapper.user.UserWrapper;

public class SignupHandler implements Handler<UserWrapper> {

	@Override
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
					throw new DBException("Customer Creation Failed.");
				}

				AccountType accountType = data.getAccountType();
				Long branchId = data.getBranchId();

				if (BasicUtil.isNull(accountType) || BasicUtil.isNull(branchId)) {
					throw new InputException("AccountType and BranchId must be provided.");
				}
				
				if (BasicUtil.isNull(accountType) || !(accountType == AccountType.SAVINGS || accountType == AccountType.CURRENT
						|| accountType == AccountType.FIXED_DEPOSIT)) {
					throw new InputException("Invalid or unsupported account type.");
				}

				Request request = new Request();
				request.setCustomerId(userId);
				request.setBranchId(branchId);
				request.setAccountType(accountType);
				request.setBalance(0.0);  // Balance set to zero initially
				request.setModifiedBy(userId);

				RequestDAO requestDao = new RequestDAO();
				Map<String, Object> requestResult = requestDao.createRequest(request);
				if (BasicUtil.isNull(requestResult)) {
					throw new DBException("Account request creation failed.");
				}

				DBUtil.commit();

				System.out.println("New user signed up with request\nUser Id: " + userId);

				Map<String, Object> response = new HashMap<>();
				response.put("message", "Signup Successful. Account request submitted.");
				response.put("userId", userId);
				response.put("role", userType);
				response.put("requestId", requestResult.get("requestId"));

				return response;

			} catch (Exception e) {
				DBUtil.rollback();
				throw e;
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
