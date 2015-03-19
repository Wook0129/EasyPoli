package database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import datamodel.Topic;

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
	
	public Topic[] retrieveAllTopics(){
		Vector<Topic> v = new Vector<Topic>();
		Topic[] topics = null;
		try{
			String sql = "SELECT * FROM "+tableName;
			ResultSet rs = stmt.executeQuery(sql);

			while(rs.next()){
				//Retrieve by column name
				Topic topic = new Topic(rs.getString("index"),rs.getString("mainArticle"),rs.getString("articleSet"),rs.getString("startDate"));
				v.add(topic);
			}
			rs.close();
			
			Object[] o = v.toArray();
			topics = new Topic[o.length];
			for(int cnt=0; cnt<o.length; cnt++){
				topics[cnt] = (Topic)o[cnt];
			}
		}catch(Exception e){e.printStackTrace();}
		return topics;
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
