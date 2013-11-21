package org.cmpe283.finalproject.db;

import java.sql.Connection;
import java.sql.DriverManager;

public class MySQLDBUtil {

	public Connection getConnection() throws Exception {
	    String driver = "org.gjt.mm.mysql.Driver";
	    String url = "jdbc:mysql://localhost/cmpe283-analysisdata";
	    String username = "root";
	    String password = "";
	    Class.forName(driver);
	    Connection conn = DriverManager.getConnection(url, username, password);
	    return conn;
 }
}
