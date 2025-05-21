package com.viiva.handler.branch;

import java.util.HashMap;
import java.util.Map;
import com.viiva.dao.branch.BranchDAO;
import com.viiva.exceptions.DBException;
import com.viiva.exceptions.InputException;
import com.viiva.handler.Handler;
import com.viiva.pojo.branch.Branch;
import com.viiva.util.BasicUtil;
import com.viiva.util.DBUtil;
import com.viiva.util.InputValidator;

public class BranchHandler implements Handler<Branch> {

	@Override
	public Object handle(String methodAction, Branch data) throws Exception {

		switch (methodAction) {

		case "POST":
			try {
				if (!BasicUtil.isNull(data)) {
					StringBuilder validationbranch = InputValidator.validateAddress(data);

					if (!validationbranch.toString().isEmpty()) {
						throw new InputException("Invalid Input(s) found: " + validationbranch);
					}
					BranchDAO branchDao = new BranchDAO();
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
				} else {
					throw new InputException("Null Input.");
				}
			} catch (Exception e) {
				DBUtil.rollback();
				throw (Exception) e;
			}

		case "GET":
			try {
				if (!BasicUtil.isNull(data)) {
					long branchId = data.getBranchId();

					if (BasicUtil.isBlank(branchId)) {
						throw new InputException("Null/Empty Branch Id");
					}

					BranchDAO branchDao = new BranchDAO();
					Branch branch = branchDao.getBranchById(branchId);

					if (BasicUtil.isNull(branch)) {
						throw new DBException("Fetching Branch details failed.");
					}

					DBUtil.commit();

					Map<String, Object> responseData = new HashMap<String, Object>();

					responseData.put("message", "Success");
					responseData.put("branchId", branch.getBranchId());
					responseData.put("managerId", branch.getManagerId());
					responseData.put("ifscCode", branch.getIfscCode());
					responseData.put("locality", branch.getLocality());
					responseData.put("district", branch.getDistrict());
					responseData.put("state", branch.getState());

					return responseData;
				} else {
					throw new InputException("Null Input.");
				}

			} catch (Exception e) {
				DBUtil.rollback();
				throw (Exception) e;
			}

		case "PUT":
			try {
				if (!BasicUtil.isNull(data)) {
					StringBuilder validationbranch = InputValidator.validateAddress(data);

					if (!validationbranch.toString().isEmpty()) {
						throw new InputException("Invalid Input(s) found: " + validationbranch);
					}

					BranchDAO branchDao = new BranchDAO();
					Branch branch = branchDao.updateBranch(data);

					if (BasicUtil.isNull(branch)) {
						throw new DBException("Updating Branch details failed.");
					}

					DBUtil.commit();
					
					Map<String, Object> responseData = new HashMap<String, Object>();
					responseData.put("message", "Success");
					responseData.put("branchId", branch.getBranchId());
					responseData.put("managerId", branch.getManagerId());
					responseData.put("ifscCode", branch.getIfscCode());
					responseData.put("locality", branch.getLocality());
					responseData.put("district", branch.getDistrict());
					responseData.put("state", branch.getState());
					responseData.put("modifiedBy", branch.getModifiedBy());
					responseData.put("modifiedTime", branch.getModifiedTime());
					
					
					return responseData;
					
				} else {
					throw new InputException("Null Input.");
				}

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
