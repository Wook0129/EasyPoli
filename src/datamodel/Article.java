package datamodel;

public class Article {
	
	private String index;
	private String title;
	private String content;
	private String date;
	private String topicNum;
	
	public Article(String title, String content, String date){ // 크롤링할때 이용
		this.title = title;
		this.content = content;
		this.date = date;
	}
	public Article(String index, String title, String content, String date, String topicNum){ //기사를 DB에서 불러올 때 이용
		this.index = index;
		this.title = title;
		this.content = content;
		this.date = date;
		this.topicNum = topicNum;
	}
	public String getIndex(){
		return index;
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
	public WordVector getTermVector(){
		WordVector termVector = new WordVector(this.getTitle() + " " + this.getContent());
		return termVector;
	}
}