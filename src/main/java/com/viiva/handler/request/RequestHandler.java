package com.viiva.handler.request;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.viiva.dao.request.RequestDAO;
import com.viiva.exceptions.AuthException;
import com.viiva.exceptions.DBException;
import com.viiva.exceptions.InputException;
import com.viiva.handler.Handler;
import com.viiva.pojo.account.AccountType;
import com.viiva.pojo.request.Request;
import com.viiva.session.SessionAware;
import com.viiva.util.BasicUtil;
import com.viiva.util.DBUtil;

public class RequestHandler implements Handler<Request> {

	@Override
	public Object handle(String methodAction, Request requestData) throws Exception {

		if (!(requestData instanceof SessionAware)) {
			throw new AuthException("Unauthorized: Session not found.");
		}

		byte sessionRole = requestData.getSessionRole();
		long sessionUserId = requestData.getSessionUserId();
		long sessionBranchId = requestData.getSessionBranchId();

		switch (methodAction) {

		case "POST":
			try {
				if (sessionUserId != requestData.getCustomerId()) {
					throw new AuthException("Access Denied: Unauthorized access to request account.");
                }
				if (BasicUtil.isNull(requestData)) {
					throw new InputException("Invalid (Null) Input.");
				}

				if (requestData.getBalance() <= 0 || BasicUtil.isNull(requestData.getBalance())) {
					throw new InputException("Balance must be greater than 0.");
				}

				AccountType type = requestData.getAccountType();
				if (type != AccountType.CURRENT && type != AccountType.SAVINGS && type != AccountType.FIXED_DEPOSIT) {
					throw new InputException("Invalid Account Type.");
				}

				Request request = (Request) requestData;
				RequestDAO requestDao = new RequestDAO();
				Map<String, Object> result = requestDao.createRequest(request);

				if (BasicUtil.isNull(result)) {
					throw new DBException("Couldn't request for an account");
				}

				DBUtil.commit();

				Map<String, Object> responseData = new HashMap<String, Object>();
				responseData.put("message", "Request created for an account.");
				responseData.put("requestID", result.get("requestId"));
				responseData.put("customerId", result.get("customerId"));
				responseData.put("status", result.get("status"));

				return responseData;

			} catch (Exception e) {
				DBUtil.rollback();
				throw (Exception) e;
			}

		case "GET":
			try {
				if (sessionRole != 2 && sessionRole != 3 && sessionRole != 4) {
					throw new AuthException("Access Denied: Unauthorized to view requests.");
				}
				if (BasicUtil.isNull(requestData)) {
					throw new InputException("Invalid (Null) Input.");
				}
				RequestDAO requestDao = new RequestDAO();
				long requestId = requestData.getRequestId();
				if (BasicUtil.isBlank(requestId)) {
					throw new InputException("Null/Empty Request Id");
				}

				if (requestId == 0) {
					List<Request> requests = requestDao.getBranchRequests(sessionBranchId);

					if (requests == null || requests.isEmpty()) {
						throw new DBException("No requests found for your branch.");
					}

					List<Map<String, Object>> resultList = new ArrayList<>();
					for (Request req : requests) {
						Map<String, Object> reqMap = new HashMap<>();
						reqMap.put("requestId", req.getRequestId());
						reqMap.put("customerId", req.getCustomerId());
						reqMap.put("branchId", req.getBranchId());
						reqMap.put("accountType", req.getAccountType());
						reqMap.put("balance", req.getBalance());
						reqMap.put("status", req.getStatus());
						reqMap.put("remarks", req.getRemarks());
						resultList.add(reqMap);
					}

					DBUtil.commit();

					Map<String, Object> response = new HashMap<>();
					response.put("message", "Branch requests fetched successfully.");
					response.put("requests", resultList);
					return response;
				} else {

					Request request = requestDao.getRequestById(requestId);

					if (BasicUtil.isNull(request)) {
						throw new DBException("Request not found.");
					}
					
					if (request.getBranchId() != sessionBranchId) {
			            throw new AuthException("Access Denied: Unauthorised for fetching request from other branch.");
			        }

					DBUtil.commit();

					Map<String, Object> responseData = new HashMap<String, Object>();
					responseData.put("message", "Request Fetched Successfully.");
					responseData.put("requestId", request.getRequestId());
					responseData.put("customerId", request.getCustomerId());
					responseData.put("branchId", request.getBranchId());
					responseData.put("accountType", request.getAccountType());
					responseData.put("balance", request.getBalance());
					responseData.put("status", request.getStatus());
					responseData.put("remarks", request.getRemarks());

					return responseData;
				}
			} catch (Exception e) {
				DBUtil.rollback();
				throw (Exception) e;
			}

		case "PUT":
			try {
				if (BasicUtil.isNull(requestData)) {
					throw new InputException("Invalid (Null) Input.");
				}
				RequestDAO requestDao = new RequestDAO();

				Request existingRequest = requestDao.getRequestById(requestData.getRequestId());

				if (BasicUtil.isNull(existingRequest)) {
					throw new DBException("No such request found.");
				}

				if (existingRequest.getBranchId() != sessionBranchId) {
					throw new AuthException("Unauthorized: Cannot modify request from another branch.");
				}

				requestData.setModifiedBy(sessionUserId);

				Map<String, Object> updatedRequest = requestDao.updateRequest(requestData);
				if (BasicUtil.isNull(updatedRequest)) {
					throw new DBException("Request not found.");
				}

				DBUtil.commit();

				updatedRequest.put("message", "Request Updated Successfully");
				return updatedRequest;
			} catch (Exception e) {
				DBUtil.rollback();
				throw (Exception) e;
			}

		default:
			throw new Exception("Unsupported method action: " + methodAction);
		}

	}

	@Override
	public Class<Request> getRequestType() {
		return Request.class;
	}

}
