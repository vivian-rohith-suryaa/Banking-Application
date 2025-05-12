package com.viiva.handler.signin;

import java.util.HashMap;
import java.util.Map;
import com.viiva.dao.user.UserDAO;
import com.viiva.exceptions.AuthException;
import com.viiva.exceptions.DBException;
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
				if (!BasicUtil.isNull(requestData)) {
					String validationResult = InputValidator.validateSignin(requestData).toString();

					if (!validationResult.isEmpty()) {
						throw new AuthException("Invalid Input(s) found: " + validationResult);
					}

					UserDAO userDao = new UserDAO();

					String email = requestData.getEmail();

					Map<String, Object> result = userDao.authenticate(email);
					System.out.println(result);
					String hashedPassword = (String) result.get("password");

					if (!BasicUtil.isNull(hashedPassword)) {
						if (BasicUtil.checkPassword(requestData.getPassword(), hashedPassword)) {

							result.remove("password");

							if (!BasicUtil.isNull(result)) {

								DBUtil.commit();

								Map<String, Object> responseData = new HashMap<>();

								responseData.put("message", "Signin Successful");
								responseData.put("userId", result.get("userId"));
								responseData.put("userType", result.get("userType"));

								return responseData;
							} else {
								DBUtil.rollback();
								throw new DBException("Failed to fetch session details.");
							}
						} else {
							DBUtil.rollback();
							throw new AuthException("Passwords do not match.");
						}
					} else {
						DBUtil.rollback();
						throw new AuthException("Signing in failed. No valid user found for this email: " + email);
					}
				} else {
					throw new InputException("Null Input.");
				}
			} catch (Exception e) {
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
