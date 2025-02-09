package projects.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import projects.exception.DbException;

public class DbConnection {

	private static final String SCHEMA = "recipes";
	private static final String USER = "recipes";
	private static final String PASSWORD = "recipes";
	private static final String HOST = "localhost";
	private static final int PORT = 3306;
	
	public static Connection getConnection() {
		String url = String.format("jdbc:mysql://%s:%d/%s?user=%s&password=%s&useSSL=true&allowPublicKeyRetrieval=true", 
				HOST, PORT, SCHEMA, USER, PASSWORD);
		
	System.out.println("Connecting with url=" + url);
	
	try {
		Connection conn = DriverManager.getConnection(url);
		System.out.println("Successfully Connected!");
		return conn;
	} catch (SQLException e) {
		throw new DbException(e);
	}
	}
}
