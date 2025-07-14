package com.viiva.handler.user;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.viiva.dao.account.AccountDAO;
import com.viiva.dao.customer.CustomerDAO;
import com.viiva.dao.user.UserDAO;
import com.viiva.exceptions.AuthException;
import com.viiva.exceptions.DBException;
import com.viiva.exceptions.InputException;
import com.viiva.handler.Handler;
import com.viiva.pojo.account.AccountType;
import com.viiva.pojo.customer.Customer;
import com.viiva.pojo.request.Request;
import com.viiva.pojo.user.User;
import com.viiva.session.SessionAware;
import com.viiva.util.BasicUtil;
import com.viiva.util.DBUtil;
import com.viiva.util.InputValidator;
import com.viiva.wrapper.user.UserWrapper;

public class UserHandler implements Handler<UserWrapper> {

	@Override
	public Object handle(String methodAction, UserWrapper data) throws Exception {

		if (!(data instanceof SessionAware)) {
			throw new AuthException("Unauthorized: Session not found.");
		}
		long sessionUserId = data.getSessionUserId();
		byte sessionRole = data.getSessionRole();

		switch (methodAction) {

		case "GET":
			try {
				if (BasicUtil.isNull(data)) {
					throw new InputException("Invalid (Null) Input.");
				}

				if (BasicUtil.isNull(data.getUser()) || BasicUtil.isBlank(data.getUser().getUserId())) {
					return getAllUsers(data);
				}

				long userId = data.getUser().getUserId();
				if (BasicUtil.isBlank(userId)) {
					throw new InputException("Null/Empty User Id");
				}

				if (sessionUserId != data.getUser().getUserId()) {
					throw new AuthException("Access Denied: Unauthorized access to user details.");
				}
				UserDAO userDao = new UserDAO();
				User user = userDao.getUserById(userId);

				if (BasicUtil.isNull(user)) {
					throw new DBException("User Profile not found.");
				}

				CustomerDAO customerDao = new CustomerDAO();

				Customer customer = customerDao.getCustomerById(userId);

				if (BasicUtil.isNull(customer)) {
					throw new DBException("User Profile not found.");
				}

				DBUtil.commit();

				Map<String, Object> responseData = new HashMap<String, Object>();

				responseData.put("message", "User Fetched Successfully");
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
				responseData.put("modifiedBy", user.getModifiedBy());
				responseData.put("modifiedTime", user.getModifiedTime());

				return responseData;
			} catch (Exception e) {
				DBUtil.rollback();
				throw (Exception) e;
			}

		case "POST":
			try {
				if (BasicUtil.isNull(data) || BasicUtil.isNull(data.getUser())
						|| BasicUtil.isNull(data.getCustomer())) {
					throw new InputException("Invalid (Null) Input.");
				}

				if (sessionRole <= 1) {
					throw new AuthException("Access Denied: Unauthorized Access to create new User.");
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

				Customer customer = data.getCustomer();
				customer.setCustomerId(userId);

				CustomerDAO customerDao = new CustomerDAO();
				if (!customerDao.signupCustomer(customer)) {
					throw new DBException("Customer Creation Failed.");
				}

				Long sessionBranchId = data.getSessionBranchId();

				AccountType accountType = data.getAccountType();
				Long branchId = data.getBranchId();
				Double balance = data.getBalance();

				if (BasicUtil.isNull(accountType) || BasicUtil.isNull(branchId)) {
					throw new InputException("AccountType and BranchId must be provided.");
				}

				if (BasicUtil.isNull(accountType) || !(accountType == AccountType.SAVINGS
						|| accountType == AccountType.CURRENT || accountType == AccountType.FIXED_DEPOSIT)) {
					throw new InputException("Invalid or unsupported account type.");
				}

				if (BasicUtil.isNull(balance) || balance <= 0.0) {
					throw new InputException("Balance must be greater than 0.");
				}

				if ((sessionRole == 2 || sessionRole == 3) && !branchId.equals(sessionBranchId)) {
					throw new AuthException("Access Denied: Cannot create accounts outside your own branch.");
				}

				Request request = new Request();
				request.setCustomerId(userId);
				request.setBranchId(branchId);
				request.setAccountType(accountType);
				request.setBalance(balance);
				request.setModifiedBy(sessionUserId);

				AccountDAO accountDao = new AccountDAO();
				Map<String, Object> accountResult = accountDao.createAccount(request);
				if (BasicUtil.isNull(accountResult)) {
					throw new DBException("Account creation failed.");
				}

				DBUtil.commit();

				System.out.println("New user signed up with request\nUser Id: " + userId);

				Map<String, Object> response = new HashMap<>();
				response.put("message", "Signup Successful. Account created.");
				response.put("userId", userId);
				response.put("accountId", accountResult.get("accountId"));

				return response;

			} catch (Exception e) {
				DBUtil.rollback();
				throw (Exception) e;
			}

		case "PUT":

			try {
				if (BasicUtil.isNull(data)) {
					throw new InputException("Invalid (Null) Input.");
				}

				String validationResult;
				if (sessionRole < 2) {

					validationResult = InputValidator.validateUser(data).toString();
				} else {
					validationResult = InputValidator.validateEmployee(data.getUser()).toString();
				}

				if (sessionUserId != data.getUser().getUserId()) {
					throw new AuthException("Access Denied: Unauthorized Access to modify user details.");
				}

				if (!validationResult.isEmpty()) {
					throw new InputException("Invalid Input(s) found: " + validationResult);
				}

				User user = data.getUser();
				UserDAO userDao = new UserDAO();

				user.setModifiedBy(sessionUserId);

				User updatedUser = userDao.updateUser(user);
				if (BasicUtil.isNull(updatedUser)) {
					throw new DBException("Updating User details failed.");
				}

				Map<String, Object> responseData = new HashMap<String, Object>();
				responseData.put("message", "User Updated Successfully");
				responseData.put("userId", updatedUser.getUserId());
				responseData.put("name", updatedUser.getName());
				responseData.put("email", updatedUser.getEmail());
				responseData.put("phone", updatedUser.getPhone());
				responseData.put("gender", updatedUser.getGender());
				responseData.put("modified_time", updatedUser.getModifiedTime());
				responseData.put("modified_by", updatedUser.getModifiedBy());

				Customer customer = data.getCustomer();
				if (customer != null) {
					customer.setCustomerId(user.getUserId());
					CustomerDAO customerDao = new CustomerDAO();
					Customer updatedCustomer = customerDao.updateCustomer(customer);
					if (BasicUtil.isNull(updatedCustomer)) {
						throw new DBException("Updating Customer details failed.");
					}

					responseData.put("dob", updatedCustomer.getDob());
					responseData.put("aadhar", updatedCustomer.getAadhar());
					responseData.put("pan", updatedCustomer.getPan());
					responseData.put("address", updatedCustomer.getAddress());
				}

				DBUtil.commit();
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

	private Object getAllUsers(UserWrapper data) throws Exception {

		Map<String, Object> session = data.getSessionAttributes();
		byte role = Byte.parseByte(session.get("role").toString());

		Long branchId = session.containsKey("branchId") ? Long.parseLong(session.get("branchId").toString()) : null;
		Map<String, String> queryParams = data.getQueryParams();

		UserDAO userDao = new UserDAO();
		CustomerDAO customerDao = new CustomerDAO();

		List<User> users = userDao.getAllUsers(role, branchId, queryParams);

		if (users.isEmpty()) {
			throw new DBException("No Users Found.");
		}

		List<Map<String, Object>> resultList = new ArrayList<>();

		for (User user : users) {
			Customer customer = customerDao.getCustomerById(user.getUserId());

			if (BasicUtil.isNull(customer)) {
				throw new DBException("Fetching User Details Failed.");
			}

			Map<String, Object> userMap = new HashMap<>();
			userMap.put("userId", user.getUserId());
			userMap.put("name", user.getName());
			userMap.put("email", user.getEmail());
			userMap.put("phone", user.getPhone());
			userMap.put("gender", user.getGender());
			userMap.put("status", user.getStatus());
			userMap.put("type", user.getType());
			userMap.put("dob", customer != null ? customer.getDob() : null);
			userMap.put("aadhar", customer != null ? customer.getAadhar() : null);
			userMap.put("pan", customer != null ? customer.getPan() : null);
			userMap.put("address", customer != null ? customer.getAddress() : null);

			resultList.add(userMap);
		}

		Map<String, Object> result = new HashMap<>();
		result.put("message", "Users fetched successfully.");
		result.put("users", resultList);
		return result;
	}

}
