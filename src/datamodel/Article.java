package datamodel;

public class Article {
	
	private String index;
	private WordVector termVector;
	private String title;
	private String content;
	private String date;
	private String topicNum;
	
	public Article(){
		
	}
	public Article(String title, String content, String date){ // Use when Crawl -> DB Insert
		this.title = title;
		this.content = content;
		this.date = date;
	}
	public Article(String index, String title, String content, String date, String topicNum){ // Use when DB -> Memory
		this.index = index;
		this.termVector = new WordVector(title + " " + content);
		this.title = title;
		this.content = content;
		this.date = date;
		this.topicNum = topicNum;
	}
	public String getIndex(){
		return index;
	}
	public WordVector getTermVector(){
		return termVector;
	}
	public String getTitle(){
		return title;
	}
	public String getContent(){
		return content;
	}
	public String getDate(){
		return date;
	}
	public String getTopicNum(){
		return topicNum;
	}
	public void setTopicNum(String topicNum){
		this.topicNum = topicNum;
	}
	@Override
	public String toString(){
		return this.index;
	}
}