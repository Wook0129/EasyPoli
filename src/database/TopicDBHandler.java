package database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import datamodel.StructuredArticle;
import datamodel.Topic;

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
	
	public Topic[] retrieveAllTopics(){
		Vector v = new Vector();
		Topic[] topics = null;
		try{
			String sql = "SELECT * FROM "+tableName;
			ResultSet rs = stmt.executeQuery(sql);

			while(rs.next()){
				//Retrieve by column name
				Topic topic = new Topic(rs.getString("index"),rs.getString("centralVector"),rs.getString("articleSet"),rs.getString("startDate"));
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
