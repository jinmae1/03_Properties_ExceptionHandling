package com.kh.common;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOError;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLRecoverableException;
import java.util.Properties;

public class JdbcTemplate {

	private static String driverClass;
	private static String url;
	private static String user;
	private static String password;

	static {

		// resources/datasource.properties 내용 불러오기
		final String datasourceConfigPath = "resources/datasource.properties";
		Properties prop = new Properties();
		try {
			prop.load(new FileReader(datasourceConfigPath));
			driverClass = prop.getProperty("driverClass");
			url = prop.getProperty("url");
			user = prop.getProperty("user");
			password = prop.getProperty("password");

			if (driverClass == null || url == null || user == null || password == null)
				throw new NullPointerException("속성값이 없습니다. " + datasourceConfigPath + "를 확인하세요");

		} catch (FileNotFoundException e) {
			System.err.println("파일을 찾을 수 없습니다.");
		} catch (IOException e) {
			e.printStackTrace();
		}

		// 1. driver class 등록: 프로그램 실행 시 최초 1회만 처리
		try {
			Class.forName(driverClass);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	public static Connection getConnection() {
		// 2. Connection객체 생성(auto commit false 처리)
		Connection conn = null;
		try {
			conn = DriverManager.getConnection(url, user, password);
			conn.setAutoCommit(false);
		} catch (SQLRecoverableException e) {
			System.err.println("DB서버의 주소를 확인해주세요");
		} catch (SQLException e) {
			if (e.getMessage().contains("username/password")) {
				System.err.println(e.getMessage());
				System.err.println("아이디/비밀번호를 확인해주세요");
			}
			if (e.getMessage().contains("locked")) {
				System.err.println(e.getMessage());
				System.err.println("계정이 잠겼습니다.");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return conn;
	}

	public static void commit(Connection conn) {
		try {
			if (conn != null && !conn.isClosed())
				conn.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void rollback(Connection conn) {
		try {
			if (conn != null && !conn.isClosed())
				conn.rollback();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void close(Connection conn) {
		try {
			if (conn != null && !conn.isClosed())
				conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void close(PreparedStatement pstmt) {
		try {
			if (pstmt != null && !pstmt.isClosed())
				pstmt.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void close(ResultSet rset) {
		try {
			if (rset != null && !rset.isClosed())
				rset.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
