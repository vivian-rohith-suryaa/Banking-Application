package com.viiva.handler.request;

import java.util.HashMap;
import java.util.Map;
import com.viiva.dao.request.RequestDAO;
import com.viiva.exceptions.DBException;
import com.viiva.exceptions.InputException;
import com.viiva.handler.Handler;
import com.viiva.pojo.request.Request;
import com.viiva.util.BasicUtil;
import com.viiva.util.DBUtil;

public class RequestHandler implements Handler<Request> {

	@Override
	public Object handle(String methodAction, Request requestData) throws Exception {
		switch (methodAction) {

		case "POST":
			try {
				if (!BasicUtil.isNull(requestData)) {
					Request request = (Request) requestData;
					RequestDAO requestDao = new RequestDAO();
					Map<String, Object> result = requestDao.createRequest(request);
					
					if (BasicUtil.isNull(result)) {
						throw new DBException("Couldn't request for an account");
					}
					
					DBUtil.commit();
					
					Map<String, Object> responseData = new HashMap<String, Object>();
					responseData.put("message", "success");
					responseData.put("requestID", result.get("requestId"));
					responseData.put("customerId", result.get("customerId"));
					responseData.put("status", result.get("status"));
					
					return responseData;
				}
			} catch (Exception e) {
				DBUtil.rollback();
				throw (Exception) e;
			}
			
		case "GET":
			try {
				if (!BasicUtil.isNull(requestData)) {
					long requestId = requestData.getRequestId();
					if (BasicUtil.isBlank(requestId)) {
						throw new InputException("Null/Empty Request Id");
					}
					RequestDAO requestDao = new RequestDAO();
					Request request = requestDao.getRequestById(requestId);
					
					if(BasicUtil.isNull(request)) {
						throw new DBException("Fetching Request details failed.");
					}
					
					DBUtil.commit();

					Map<String, Object> responseData = new HashMap<String, Object>();
					responseData.put("message","Success");
					responseData.put("requestId",request.getRequestId());
					responseData.put("customerId",request.getCustomerId());
					responseData.put("branchId",request.getBranchId());
					responseData.put("accountType",request.getAccountType());
					responseData.put("balance",request.getBalance());
					responseData.put("status",request.getStatus());
					responseData.put("remarks",request.getRemarks());
					
					return responseData;
				}
			} catch (Exception e) {
				DBUtil.rollback();
				throw (Exception) e;
			}
			
		case "PUT":
			try {
				if (!BasicUtil.isNull(requestData)) {
					RequestDAO requestDao = new RequestDAO();
					Map<String,Object> updatedRequest = requestDao.updateRequest(requestData);
					if (BasicUtil.isNull(updatedRequest)) {
						throw new DBException("Updating User details failed.");
					}
					
					DBUtil.commit();

					updatedRequest.put("message", "Success");
					return updatedRequest;
				}
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
