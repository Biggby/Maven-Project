package projects;

import java.sql.Connection;

import projects.dao.DbConnection;

public class Projects {

	public Projects() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		DbConnection.getConnection();
	}
	
}
