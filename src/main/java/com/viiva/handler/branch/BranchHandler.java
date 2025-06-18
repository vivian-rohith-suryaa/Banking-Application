package com.viiva.handler.branch;

import java.util.HashMap;
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
		long sessionUserId = data.getSessionUserId();
		long sessionBranchId = data.getSessionBranchId();

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
				long branchId = data.getBranchId();

				if (BasicUtil.isBlank(branchId)) {
					throw new InputException("Null/Empty Branch Id");
				}

				BranchDAO branchDao = new BranchDAO();
				Branch branch = branchDao.getBranchById(branchId);

				if (BasicUtil.isNull(branch)) {
					throw new DBException("Branch Not Found.");
				}

				DBUtil.commit();

				Map<String, Object> responseData = new HashMap<String, Object>();

				responseData.put("message", "Branch Details fetched successfully.");
				responseData.put("branchId", branch.getBranchId());
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

		case "PUT":
			try {
				if (sessionRole != 4 || sessionRole != 3) {
					if (BasicUtil.isNull(sessionBranchId) || sessionBranchId != data.getBranchId()) {
						throw new AuthException("Access Denied: Unauthorised to update a branch.");
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
