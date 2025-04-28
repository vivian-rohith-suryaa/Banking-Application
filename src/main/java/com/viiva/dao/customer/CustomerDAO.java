package com.viiva.dao.customer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import com.viiva.exceptions.DBException;
import com.viiva.pojo.customer.Customer;
import com.viiva.util.DBUtil;

public class CustomerDAO {

	public boolean signupCustomer(Customer customer) throws Exception {
		String query = "INSERT INTO customer (customer_id, dob, aadhar, pan, address) VALUES (?,?,?,?,?)";

		try (Connection conn = DBUtil.getConnection(); PreparedStatement pstmt = DBUtil.prepare(conn, query)) {

			pstmt.setLong(1, customer.getCustomerId());
			pstmt.setString(2, customer.getDob());
			pstmt.setString(3, customer.getAadhar());
			pstmt.setString(4, customer.getPan());
			pstmt.setString(5, customer.getAddress());

			int rows = DBUtil.executeUpdate(pstmt);
			return rows > 0;
		} catch (SQLException e) {
			throw new DBException("Error while signing up the user.", e);
		}
	}

}
