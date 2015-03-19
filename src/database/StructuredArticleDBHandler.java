package database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import datamodel.StructuredArticle;
import datamodel.WordVector;

public class StructuredArticleDBHandler extends DBHandler{

	final private static String tableName = "structuredarticle";

	public StructuredArticleDBHandler(){
		connect();
		try {stmt = conn.createStatement();}
		catch (SQLException e) {e.printStackTrace();}
	}

	public void insert(WordVector wordVector, String topicNum, String date) {
		try{
			String sql = "INSERT INTO "+tableName+" (termvector, topicnum, date) VALUES ('"+wordVector.toString()+"','"+topicNum+"','"+date+"')";
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
	
	public void update(String index, String colName, String modified){
		try{
			String sql = "UPDATE "+tableName+" SET "+colName+" = '"+modified+"' WHERE `index` = "+index;
			stmt.execute(sql);
		}catch(Exception e){e.printStackTrace();}
	}
	
	public void delete(String colName, String value){
		try{
			String sql= "DELETE FROM "+tableName+" WHERE "+colName+"= '"+value+"'";
			stmt.execute(sql);
		}catch(Exception e){e.printStackTrace();}
	}
	public StructuredArticle[] getAllArticles(){
		StructuredArticle[] sArticles = new StructuredArticle[0];
		Vector v = new Vector();
		try{
			String sql = "SELECT * FROM "+tableName;
			ResultSet rs = stmt.executeQuery(sql);

			while(rs.next()){
				//Retrieve by column name
				String index = rs.getString("index");
				String jsonTermVector = rs.getString("termVector");
				String topicNum = rs.getString("topicNum");
				String date = rs.getString("date");
				//StructuredArticle 객체로 저장
				StructuredArticle sArticle = new StructuredArticle(index, jsonTermVector,topicNum,date);
				v.add(sArticle);
			}
			rs.close();
			Object[] o = v.toArray();
			sArticles = new StructuredArticle[o.length];
			for(int cnt = 0; cnt<o.length; cnt++){
				sArticles[cnt] = (StructuredArticle)o[cnt];
			}
		}catch(Exception e){e.printStackTrace();}
		return sArticles;
	}
	public StructuredArticle[] getArticlesInDate(String givenDate){
		StructuredArticle[] sArticles = new StructuredArticle[30]; //네이버 많이 본 뉴스는 하루에 30위까지 제공됨
		try{
			String sql = "SELECT * FROM "+tableName+" WHERE date = '"+givenDate+"'";
			ResultSet rs = stmt.executeQuery(sql);
			
			int cnt = 0;
			while(rs.next()){
				//Retrieve by column name
				String index = rs.getString("index");
				String jsonTermVector = rs.getString("termVector");
				String topicNum = rs.getString("topicNum");
				String date = rs.getString("date");
				//StructuredArticle 객체로 저장
				sArticles[cnt] = new StructuredArticle(index, jsonTermVector,topicNum,date);
				cnt++;
			}
			rs.close();
		}catch(Exception e){e.printStackTrace();}
		return sArticles;
	}
	public StructuredArticle[] getArticlesInTopic(String givenTopicIndex){
		StructuredArticle[] sArticles = new StructuredArticle[30]; //한 토픽에 최대 30개 기사
		try{
			String sql = "SELECT * FROM "+tableName+" WHERE `topicnum` = "+givenTopicIndex;
			ResultSet rs = stmt.executeQuery(sql);
			
			int cnt = 0;
			while(rs.next()){
				//Retrieve by column name
				String index = rs.getString("index");
				String jsonTermVector = rs.getString("termVector");
				String topicNum = rs.getString("topicNum");
				String date = rs.getString("date");
				//StructuredArticle 객체로 저장
				sArticles[cnt] = new StructuredArticle(index, jsonTermVector,topicNum,date);
				cnt++;
			}
			rs.close();
		}catch(Exception e){e.printStackTrace();}
		return sArticles;
	}
}
