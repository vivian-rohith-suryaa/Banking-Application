package com.viiva.handler.branch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.viiva.dao.branch.BranchDAO;
import com.viiva.exceptions.AuthException;
import com.viiva.exceptions.DBException;
import com.viiva.exceptions.InputException;
import com.viiva.handler.Handler;
import com.viiva.pojo.branch.Branch;
import com.viiva.session.SessionAware;
import com.viiva.util.BasicUtil;
import com.viiva.util.DBUtil;
import com.viiva.util.InputValidator;

public class BranchHandler implements Handler<Branch> {

	@Override
	public Object handle(String methodAction, Branch data) throws Exception {

		if (!(data instanceof SessionAware)) {
			throw new AuthException("Unauthorized: Session not found.");
		}

		byte sessionRole = data.getSessionRole();
		Long sessionUserId = data.getSessionUserId();


		switch (methodAction) {

		case "POST":
			try {
				if (sessionRole != 4) {
					throw new AuthException("Access Denied: Unauthorised to register a branch.");
				}

				if (BasicUtil.isNull(data)) {
					throw new InputException("Invalid (Null) Input.");
				}
				StringBuilder validationbranch = InputValidator.validateAddress(data);

				if (!validationbranch.toString().isEmpty()) {
					throw new InputException("Invalid Input(s) found: " + validationbranch);
				}

				BranchDAO branchDao = new BranchDAO();
				data.setModifiedBy(sessionUserId);
				Map<String, Object> branch = branchDao.addBranch(data);

				if (BasicUtil.isNull(branch)) {
					throw new DBException("Branch Registration Failed.");
				}

				DBUtil.commit();

				System.out.println("New Branch Registered\n" + "Branch ID: " + branch.get("branchId"));

				Map<String, Object> responseData = new HashMap<>();
				responseData.put("message", "Branch Registration Successful");
				responseData.put("branchId", branch.get("branchId"));

				return responseData;
			} catch (Exception e) {
				DBUtil.rollback();
				throw (Exception) e;
			}

		case "GET":
			try {
				if (BasicUtil.isNull(data)) {
					throw new InputException("Invalid (Null) Input.");
				}
				BranchDAO branchDao = new BranchDAO();

				if (!BasicUtil.isBlank(data.getBranchId())) {
					Long branchId= data.getBranchId();
					Branch branch = branchDao.getBranchById(branchId);

					if (BasicUtil.isNull(branch)) {
						throw new DBException("Branch Not Found.");
					}

					DBUtil.commit();

					Map<String, Object> responseData = new HashMap<String, Object>();

					responseData.put("message", "Branch Details fetched successfully.");
					responseData.put("branchId", branch.getBranchId());
					responseData.put("branchName", branch.getBranchName());
					responseData.put("managerId", branch.getManagerId());
					responseData.put("ifscCode", branch.getIfscCode());
					responseData.put("locality", branch.getLocality());
					responseData.put("district", branch.getDistrict());
					responseData.put("state", branch.getState());
					responseData.put("modifiedBy", branch.getModifiedBy());
					responseData.put("modifiedTime", branch.getModifiedTime());

					return responseData;

				}
				
				Map<String, String> filters = data.getQueryParams();

				List<Branch> branchList = branchDao.getAllBranches(filters);
				if (branchList.isEmpty()) {
					throw new DBException("No branches found.");
				}

				List<Map<String, Object>> branchListResponse = new ArrayList<>();
				for (Branch branch : branchList) {
				    Map<String, Object> b = new HashMap<>();
				    b.put("branchId", branch.getBranchId());
				    b.put("branchName", branch.getBranchName());
				    b.put("managerId", branch.getManagerId());
				    b.put("ifscCode", branch.getIfscCode());
				    b.put("locality", branch.getLocality());
				    b.put("district", branch.getDistrict());
				    b.put("state", branch.getState());
				    b.put("createdTime", branch.getCreatedTime());
				    b.put("modifiedTime", branch.getModifiedTime());
				    b.put("modifiedBy", branch.getModifiedBy());
				    branchListResponse.add(b);
				}

				Map<String, Object> response = new HashMap<>();
				response.put("message", "Branches fetched successfully.");
				response.put("branches", branchListResponse);
				return response;
				
			} catch (Exception e) {
				DBUtil.rollback();
				throw (Exception) e;
			}

		case "PUT":
			try {
				Long sessionBranchId = data.getSessionBranchId();
				if (sessionRole < 3) { 
					throw new AuthException("Access Denied: Insufficient privileges.");
				}

				if (sessionRole == 3) {
					if (BasicUtil.isNull(sessionBranchId) || sessionBranchId != data.getBranchId()) {
						throw new AuthException("Access Denied: Employees can only update their own branch.");
					}
				}
				if (BasicUtil.isNull(data)) {
					throw new InputException("Invalid (Null) Input.");
				}
				StringBuilder validationbranch = InputValidator.validateAddress(data);

				if (!validationbranch.toString().isEmpty()) {
					throw new InputException("Invalid Input(s) found: " + validationbranch);
				}

				BranchDAO branchDao = new BranchDAO();
				data.setModifiedBy(sessionUserId);
				Branch branch = branchDao.updateBranch(data);

				if (BasicUtil.isNull(branch)) {
					throw new DBException("Branch not found.");
				}

				DBUtil.commit();

				Map<String, Object> responseData = new HashMap<String, Object>();
				responseData.put("message", "Branch Updated Successfully.");
				responseData.put("branchId", branch.getBranchId());
				responseData.put("branchName", branch.getBranchName());
				responseData.put("managerId", branch.getManagerId());
				responseData.put("ifscCode", branch.getIfscCode());
				responseData.put("locality", branch.getLocality());
				responseData.put("district", branch.getDistrict());
				responseData.put("state", branch.getState());
				responseData.put("modifiedBy", branch.getModifiedBy());
				responseData.put("modifiedTime", branch.getModifiedTime());

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
	public Class<Branch> getRequestType() {
		return Branch.class;
	}

}
