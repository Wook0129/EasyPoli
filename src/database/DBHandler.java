package database;

//STEP 1. Import required packages
import java.sql.*;

public abstract class DBHandler {
	// JDBC driver name and database URL
	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";  
	static final String DB_URL = "jdbc:mysql://localhost/easypoli";

	//  Database credentials
	static final String USER = "root";
	static final String PASS = "1234";

	static Connection conn = null;
	static Statement stmt = null;

	protected void connect() { //STEP 2: Register JDBC driver
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {e.printStackTrace();}
		//STEP 3: Open a connection
		System.out.println("Connecting to database...");
		try {conn = DriverManager.getConnection(DB_URL,USER,PASS);
		} catch (SQLException e) {e.printStackTrace();}
	}
	
	public void close() {  //DB 다 쓰고 나면 닫을 것.
		try{
			if(stmt!=null) stmt.close();
		}catch(SQLException se2){}
		try{
			if(conn!=null) conn.close();
		}catch(SQLException se){se.printStackTrace();}
	}
	
	public void insert(String col1, String col2, String col3){
		
	}

	public abstract void update(String colName, String original, String modified);

	public abstract void select(String col, String value);

	public abstract void delete(String col, String value);
}