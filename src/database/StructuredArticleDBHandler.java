package database;

import java.sql.ResultSet;
import java.sql.SQLException;

public class StructuredArticleDBHandler extends DBHandler{

	final private static String tableName = "structuredarticle";

	public StructuredArticleDBHandler(){
		connect();
		try {stmt = conn.createStatement();}
		catch (SQLException e) {e.printStackTrace();}
	}

	public void insert(String termVector, String topicNum, String date) {
		try{
			String sql = "INSERT INTO "+tableName+" (termvector, topicnum, date) VALUES ('"+termVector+"','"+topicNum+"','"+date+"')";
			stmt.execute(sql);
		}catch(Exception e){e.printStackTrace();}
	}
	
	public void select(String colName, String value) {
		try{
			String sql = "SELECT * FROM "+tableName+" WHERE "+colName+"= '"+value+"'";
			ResultSet rs = stmt.executeQuery(sql);

			while(rs.next()){
				//Retrieve by column name
				String index  = rs.getString("index");
				String termVector = rs.getString("termVector");
				String topicNum = rs.getString("topicNum");
				String date = rs.getString("date");
				//Display values
				System.out.print("index: " + index);
				System.out.print(", termVector: " + termVector);
				System.out.print(", topicNum: " + topicNum);
				System.out.print(", date: " + date);
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
