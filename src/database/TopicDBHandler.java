package database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

public class TopicDBHandler extends DBHandler{

	final private static String tableName = "topic";
	
	public TopicDBHandler(){
		connect();
		try {stmt = conn.createStatement();}
		catch (SQLException e) {e.printStackTrace();}
	}

	public void insert(String mainArticle, String articleSet, String startDate) {
		try{
			String sql = "INSERT INTO "+tableName+" (mainArticle, articleSet, startDate) VALUES ('"+mainArticle+"','"+articleSet+"','"+startDate+"')";
			stmt.execute(sql);
		}catch(Exception e){e.printStackTrace();}
	}

	public void update(String index, String colName, String modified){
		try{
			String sql = "UPDATE "+tableName+" SET "+colName+" = '"+modified+"' WHERE `index` = "+index;
			stmt.execute(sql);
		}catch(Exception e){e.printStackTrace();}
	}
	
	public String getMainArticleOfTopic(String idx){ //지금은 mainArticle만 갖고 오도록 구현
		String mainArticleIndex = "";
		try{
			String sql = "SELECT * FROM "+tableName+" WHERE `index` = "+idx;
			ResultSet rs = stmt.executeQuery(sql);
			while(rs.next()){
				mainArticleIndex = rs.getString("mainArticle");
			}
		}catch(Exception e){e.printStackTrace();}
		return mainArticleIndex;
	}
	
	public String[] getAllTopicIndex(){
		String[] index = {};
		try{
			String sql = "SELECT * FROM "+tableName;
			ResultSet rs = stmt.executeQuery(sql);
			Vector<String> v = new Vector<String>();
			while(rs.next()){
				v.add(rs.getString("index"));
			}
			rs.close();
			Object[] o = v.toArray();
			index = new String[o.length];
			for(int cnt=0; cnt<o.length; cnt++){
				index[cnt] = (String)o[cnt];
			}
		}catch(Exception e){e.printStackTrace();}
		return index;
	}
	
	public String lastInsertedID(){
		String lastRow = "";
		try{
			String sql = "SELECT LAST_INSERT_ID()";
			ResultSet rs = stmt.executeQuery(sql);
			if(rs.next())lastRow = rs.getString("LAST_INSERT_ID()");
			rs.close();
		}catch(Exception e){e.printStackTrace();}
		return lastRow;
	}
}
