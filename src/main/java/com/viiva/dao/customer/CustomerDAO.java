package com.viiva.dao.customer;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import com.viiva.exceptions.DBException;
import com.viiva.pojo.customer.Customer;
import com.viiva.util.DBUtil;

public class CustomerDAO {

	public boolean signupCustomer(Customer customer) {

		String query = "INSERT INTO customer (customer_id, dob, aadhar, pan, address) VALUES (?,?,?,?,?)";

		try (PreparedStatement pstmt = DBUtil.prepare(DBUtil.getConnection(), query)) {

			pstmt.setLong(1, customer.getCustomerId());
			pstmt.setString(2, customer.getDob());
			pstmt.setString(3, customer.getAadhar());
			pstmt.setString(4, customer.getPan());
			pstmt.setString(5, customer.getAddress());

			int rows = DBUtil.executeUpdate(pstmt);
			return rows > 0;
		} catch (SQLIntegrityConstraintViolationException e) {
			String message = e.getMessage();

			if (message.contains("customer.aadhar")) {
				throw new DBException("Duplicate Aadhar Number.");
			} else if (message.contains("customer.pan")) {
				throw new DBException("Duplicate PAN Number.");
			} else {
				throw new DBException("Constraint Violation occurred Database.");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DBException("Error while signing up the user.", e);
		}
	}

	public Customer getCustomerById(long customerId) {

		String query = "SELECT dob, aadhar, pan, address FROM customer WHERE customer_id = ?";
		try (PreparedStatement pstmt = DBUtil.prepare(DBUtil.getConnection(), query)) {
			pstmt.setLong(1, customerId);

			try (ResultSet rs = DBUtil.executeQuery(pstmt)) {
				while (rs.next()) {
					Customer customer = new Customer();
					customer.setDob(rs.getString("dob"));
					customer.setAadhar(rs.getString("aadhar"));
					customer.setPan(rs.getString("pan"));
					customer.setAddress(rs.getString("address"));

					return customer;
				}
				return null;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DBException("Error occurred while fetching the user details.", e);
		}

	}

	public Customer updateCustomer(Customer customer) {

		String query = "UPDATE customer SET dob=?,aadhar=?,pan=?,address=? WHERE customer_id=?";

		try (PreparedStatement pstmt = DBUtil.prepare(DBUtil.getConnection(), query)) {
			pstmt.setString(1, customer.getDob());
			pstmt.setString(2, customer.getAadhar());
			pstmt.setString(3, customer.getPan());
			pstmt.setString(4, customer.getAddress());
			pstmt.setLong(5, customer.getCustomerId());

			int rows = DBUtil.executeUpdate(pstmt);
			if (rows > 0) {
				System.out.println("Finished in the Customer DAO");
				return customer;
			}
			System.out.println("NULL in the Customer DAO");
			return null;

		} catch (SQLIntegrityConstraintViolationException e) {
			String message = e.getMessage();

			if (message.contains("customer.aadhar")) {
				throw new DBException("Duplicate Entry for Aadhar found.");
			} else if (message.contains("customer.pan")) {
				throw new DBException("Duplicate Entry for PAN found.");
			} else {
				throw new DBException("Constraint Violation occurred Database.");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DBException("Error occurred while updating the user details.", e);
		}

	}

}
