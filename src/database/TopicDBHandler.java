package database;

import java.sql.ResultSet;
import java.sql.SQLException;

public class TopicDBHandler extends DBHandler{

	final private static String tableName = "topic";

	public TopicDBHandler(){
		connect();
		try {stmt = conn.createStatement();}
		catch (SQLException e) {e.printStackTrace();}
	}

	public void insert(String centralVector, String articleSet, String startDate) {
		try{
			String sql = "INSERT INTO "+tableName+" (centralVector, articleSet, startDate) VALUES ('"+centralVector+"','"+articleSet+"','"+startDate+"')";
			stmt.execute(sql);
		}catch(Exception e){e.printStackTrace();}
	}

	public void select(String col, String value) {
		try{
			String sql = "SELECT * FROM "+tableName+" WHERE index = '"+value+"'";
			ResultSet rs = stmt.executeQuery(sql);

			while(rs.next()){
				//Retrieve by column name
				String index  = rs.getString("index");
				String centralVector = rs.getString("centralVector");
				String articleSet = rs.getString("articleSet");
				String startDate = rs.getString("startDate");
				//Display values
				System.out.print("index: " + index);
				System.out.print(", centralVector: " + centralVector);
				System.out.print(", articleSet: " + articleSet);
				System.out.print(", startDate: " + startDate);
			}
			rs.close();

		}catch(Exception e){e.printStackTrace();}
	}

	public void update(String colName, String original, String modified){
		try{
			String sql = "UPDATE "+tableName+" SET "+colName+"='"+modified+"' WHERE "+colName+" = '"+original+"'";
			stmt.execute(sql);
		}catch(Exception e){e.printStackTrace();}
	}
	
	public void delete(String colName, String value){
		try{
			String sql= "DELETE FROM "+tableName+" WHERE "+colName+"= '"+value+"'";
			stmt.execute(sql);
		}catch(Exception e){e.printStackTrace();}
	}
}