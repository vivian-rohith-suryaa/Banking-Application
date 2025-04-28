package com.viiva.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
		} catch (NamingException e) {
			throw new DBException("Failed to initialize datasource.", e);
		}
	}

	public static Connection getConnection() {
		Connection conn = threadLocal.get();
		try {
			if (BasicUtil.isNull(conn) || conn.isClosed()) {
				conn = dataSource.getConnection();
				threadLocal.set(conn);
			}
		} catch (SQLException e) {
			throw new DBException("Failed to get Database Connection.", e);
		}
		return conn;
	}

	public static void start() {
		try {
			Connection conn = getConnection();
			conn.setAutoCommit(false);
		} catch (SQLException e) {
			throw new DBException("Failed to get start Connection.", e);
		}
	}

	public static void commit() {
		try {
			Connection conn = getConnection();
			conn.commit();
		} catch (SQLException e) {
			throw new DBException("Failed to get commit Connection.", e);
		} finally {
			close();
		}
	}

	public static void rollback() {
		try {
			Connection conn = threadLocal.get();
			if (!BasicUtil.isNull(conn)) {
				conn.rollback();
			}
		} catch (SQLException e) {
			throw new DBException("Failed to get rollback Connection.", e);
		} finally {
			close();
		}
	}

	public static void close() {
		try {
			Connection conn = threadLocal.get();
			if (!BasicUtil.isNull(conn) || !conn.isClosed()) {
				conn.close();
			}
		} catch (SQLException e) {
			throw new DBException("Failed to get close Connection.", e);
		} finally {
			threadLocal.remove();
		}
	}

	public static PreparedStatement prepare(Connection conn, String query) {
		try {
			if (!BasicUtil.isNull(conn) && !BasicUtil.isBlank(query)) {
				return conn.prepareStatement(query);
			}
		} catch (SQLException e) {
			throw new DBException("Failed to create a prepareStatement.", e);
		}
		return null;
	}

	public static ResultSet executeQuery(PreparedStatement pstmnt) {
		try {
			if (!BasicUtil.isNull(pstmnt)) {
				return pstmnt.executeQuery();
			}
		} catch (SQLException e) {
			throw new DBException("Failed to execute the query.", e);
		}
		return null;
	}

	public static int executeUpdate(PreparedStatement pstmnt) {
		try {
			if (!BasicUtil.isNull(pstmnt)) {
				return pstmnt.executeUpdate();
			}
		} catch (SQLException e) {
			throw new DBException("Failed to execute the update.", e);
		}
		return 0;
	}

}
