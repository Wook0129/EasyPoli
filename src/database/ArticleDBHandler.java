package database;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ArticleDBHandler extends DBHandler{

	final private static String tableName = "article";

	public ArticleDBHandler(){
		connect();
		try {stmt = conn.createStatement();}
		catch (SQLException e) {e.printStackTrace();}
	}

	public void insert(String title, String content, String date) {
		try{
			String sql = "INSERT INTO "+tableName+" (title, content, date) VALUES ('"+title+"','"+content+"','"+date+"')";
			stmt.execute(sql);
		}catch(Exception e){e.printStackTrace();}
	}
	
	//기사 DB에서는 날짜 단위로 검색, 삭제
	public void select(String colName, String givenDate) { 
		try{
			String sql = "SELECT * FROM "+tableName+" WHERE "+colName+"= '"+givenDate+"'";
			ResultSet rs = stmt.executeQuery(sql);
			while(rs.next()){
				//Retrieve by column name
				String index  = rs.getString("index");
				String title = rs.getString("title");
				String content = rs.getString("content");
				String date = rs.getString("date");
				//Display values
				System.out.print("index: " + index);
				System.out.print(", title: " + title);
				System.out.print(", content: " + content);
				System.out.println(", date: " + date);
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