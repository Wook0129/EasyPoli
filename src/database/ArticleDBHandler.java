package database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

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
	
	public void update(String index, String colName, String value){
		try{
			String sql = "UPDATE "+tableName+" SET "+colName+"='"+value+"' WHERE `index` = "+index;
			stmt.execute(sql);
		}catch(Exception e){e.printStackTrace();}
	}
	
	//기사 하나를 가져오는 메서드
	public Article getArticle(String idx){
		Article article = null;
		try{
			String sql = "SELECT * FROM "+tableName+" WHERE `index` = "+idx;
			ResultSet rs = stmt.executeQuery(sql);

			while(rs.next()){
				String index = rs.getString("index");
				String title = rs.getString("title");
				String content = rs.getString("content");
				String date = rs.getString("date");
				String topicNum = rs.getString("topicnum");
				article = new Article(index, title, content, date, topicNum);
			}

		}catch(Exception e){e.printStackTrace();}
		return article;
	}
	
	//모든 기사를 가져오는 메서드
	public Article[] getAllArticles(){
		
		Article[] articles = null;
		Vector v = new Vector();
		try{
			String sql = "SELECT * FROM "+tableName;
			ResultSet rs = stmt.executeQuery(sql);

			while(rs.next()){
				String index = rs.getString("index");
				String title = rs.getString("title");
				String content = rs.getString("content");
				String date = rs.getString("date");
				String topicNum = rs.getString("topicnum");
				Article article = new Article(index, title, content, date, topicNum);
				v.add(article);
			}
			rs.close();
			Object[] o = v.toArray();
			articles = new Article[o.length];
			for(int cnt = 0; cnt<o.length; cnt++){
				articles[cnt] = (Article)o[cnt];
			}
		}catch(Exception e){e.printStackTrace();}
		return articles;
	}
	
	//특정 토픽에 속한 기사들을 가져오는 메서드
	public Article[] getArticlesInTopic(String givenTopicIndex){
		Article[] articles = null;
		Vector v = new Vector();
		try{
			String sql = "SELECT * FROM "+tableName+" WHERE `topicnum` = "+givenTopicIndex;
			ResultSet rs = stmt.executeQuery(sql);
			
			while(rs.next()){
				String index = rs.getString("index");
				String title = rs.getString("title");
				String content = rs.getString("content");
				String date = rs.getString("date");
				String topicNum = rs.getString("topicnum");
				Article article = new Article(index, title, content, date, topicNum);
				v.add(article);
			}
			rs.close();
			Object[] o = v.toArray();
			articles = new Article[o.length];
			for(int cnt = 0; cnt<o.length; cnt++){
				articles[cnt] = (Article)o[cnt];
			}
		}catch(Exception e){e.printStackTrace();}
		return articles;
	}
}
