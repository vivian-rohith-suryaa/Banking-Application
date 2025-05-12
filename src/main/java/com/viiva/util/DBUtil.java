package com.viiva.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import com.viiva.exceptions.DBException;

public class DBUtil {

	private static final ThreadLocal<Connection> threadLocal = new ThreadLocal<>();
	private static DataSource dataSource;

	static {
		try {
			Context initContext = new InitialContext();
			Context envContext = (Context) initContext.lookup("java:/comp/env");
			dataSource = (DataSource) envContext.lookup("jdbc/viivadb");
			System.out.println("Connection Setup initialised.\n");
		} catch (NamingException e) {
			e.printStackTrace();
			throw new DBException("Failed to initialize datasource.", e);
		}
	}

	public static Connection getConnection() {
		Connection conn = threadLocal.get();
		try {
			if (BasicUtil.isNull(conn) || conn.isClosed()) {
				conn = dataSource.getConnection();
				conn.setAutoCommit(false);
				threadLocal.set(conn);
			}
		} catch (SQLException e) {
			throw new DBException("Failed to get Database Connection.", e);
		}
		return conn;
	}

	public static void commit() {
		try {
			Connection conn = getConnection();
			conn.commit();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DBException("Failed to get commit Connection.", e);
		} finally {
			close();
		}
	}

	public static void rollback() {
		try {
			Connection conn = getConnection();
			if (!BasicUtil.isNull(conn)) {
				conn.rollback();
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DBException("Failed to get rollback Connection.", e);
		} finally {
			close();
		}
	}

	public static void close() {
		try {
			Connection conn = getConnection();
			if (!BasicUtil.isNull(conn) && !conn.isClosed()) {
				conn.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DBException("Failed to get close Connection.", e);
		} finally {
			threadLocal.remove();
		}
	}

	public static PreparedStatement prepare(Connection conn, String query) throws SQLException {

		if (!BasicUtil.isNull(conn) && !BasicUtil.isBlank(query)) {
			return conn.prepareStatement(query);
		}
		return null;
	}

	public static PreparedStatement prepareWithKeys(Connection conn, String query) throws SQLException {
		if (!BasicUtil.isNull(conn) && !BasicUtil.isBlank(query)) {
			return conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
		}
		return null;
	}

	public static ResultSet executeQuery(PreparedStatement pstmnt) throws SQLException {
		if (!BasicUtil.isNull(pstmnt)) {
			return pstmnt.executeQuery();
		}
		return null;
	}

	public static int executeUpdate(PreparedStatement pstmnt) throws SQLException {

		if (!BasicUtil.isNull(pstmnt)) {
			return pstmnt.executeUpdate();
		}
		return 0;
	}

}
