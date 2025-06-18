package com.viiva.handler.signin;

import java.util.HashMap;
import java.util.Map;
import com.viiva.dao.user.UserDAO;
import com.viiva.exceptions.AuthException;
import com.viiva.exceptions.InputException;
import com.viiva.handler.Handler;
import com.viiva.util.BasicUtil;
import com.viiva.util.DBUtil;
import com.viiva.util.InputValidator;
import com.viiva.wrapper.signin.SigninRequest;

public class SigninHandler implements Handler<SigninRequest> {

	@Override
	public Object handle(String methodAction, SigninRequest requestData) throws Exception {

		switch (methodAction) {

		case "POST":

			try {
				if (BasicUtil.isNull(requestData)) {
					throw new InputException("Invalid (Null) Input.");
				}
				String validationResult = InputValidator.validateSignin(requestData).toString();

				if (!validationResult.isEmpty()) {
					throw new AuthException("Invalid Input(s) found: " + validationResult);
				}

				UserDAO userDao = new UserDAO();

				String email = requestData.getEmail();

				Map<String, Object> result = userDao.authenticate(email);

				if (BasicUtil.isNull(result)) {
					throw new AuthException("Invalid email. No user found.");
				}
				String hashedPassword = (String) result.get("password");

				if (!BasicUtil.checkPassword(requestData.getPassword(), hashedPassword)) {
					throw new AuthException("Passwords do not match.");
				}

				result.remove("password");
				DBUtil.commit();

				Map<String, Object> responseData = new HashMap<>();
				responseData.put("message", "Signin Successful");
				responseData.put("userId", result.get("userId"));
				responseData.put("role", result.get("userType"));

				if (result.containsKey("branchId")) {
					responseData.put("branchId", result.get("branchId"));
				}

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
	public Class<SigninRequest> getRequestType() {

		return SigninRequest.class;
	}

}
