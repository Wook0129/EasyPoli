package database;

import java.sql.ResultSet;
import java.sql.SQLException;

import datamodel.Article;

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
	
	public Article[] getArticlesInDate(String givenDate){
		Article[] articles = new Article[30]; //네이버 많이 본 뉴스는 하루에 30위까지 제공됨
		try{
			String sql = "SELECT * FROM "+tableName+" WHERE date = '"+givenDate+"'";
			ResultSet rs = stmt.executeQuery(sql);
			
			int cnt = 0;
			while(rs.next()){
				//Retrieve by column name
				String title = rs.getString("title");
				String content = rs.getString("content");
				String date = rs.getString("date");
				//Article 객체로 저장
				articles[cnt] = new Article(title,content,date); //TODO: 인덱스 제거
				cnt++;
			}
			rs.close();
		}catch(Exception e){e.printStackTrace();}
		return articles;
	}
}
