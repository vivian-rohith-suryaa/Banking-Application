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

		switch (methodAction) {

		case "GET":
			try {
				if (sessionRole != 2 && sessionRole != 3 && sessionRole != 4) {
					throw new AuthException("Access Denied: Unauthorized to view requests.");
				}
				if (BasicUtil.isNull(requestData)) {
					throw new InputException("Invalid (Null) Input.");
				}
				Long sessionBranchId = requestData.getSessionBranchId();

				RequestDAO requestDao = new RequestDAO();

				if (BasicUtil.isBlank(requestData.getRequestId())) {
					return getAllRequests(requestData);
				}

				long requestId = requestData.getRequestId();

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
				responseData.put("modifiedBy", request.getModifiedBy());

				return responseData;
			} catch (Exception e) {
				DBUtil.rollback();
				throw (Exception) e;
			}

		case "PUT":
			try {
				if (BasicUtil.isNull(requestData)) {
					throw new InputException("Invalid (Null) Input.");
				}
				Long sessionBranchId = requestData.getSessionBranchId();
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

	private Object getAllRequests(Request data) throws Exception {

		Map<String, Object> session = data.getSessionAttributes();
		byte role = Byte.parseByte(session.get("role").toString());

		Long branchId = session.containsKey("branchId") ? Long.parseLong(session.get("branchId").toString()) : null;
		Map<String, String> queryParams = data.getQueryParams();

		RequestDAO requestDao = new RequestDAO();

		List<Request> requests = requestDao.getAllRequests(role, branchId, queryParams);

		if (requests.isEmpty() || requests == null) {
			throw new DBException("No Requests Found.");
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
			reqMap.put("modifiedBy", req.getModifiedBy());
			resultList.add(reqMap);
		}

		Map<String, Object> result = new HashMap<>();
		result.put("message", "Requests fetched successfully.");
		result.put("requests", resultList);
		return result;
	}

}
