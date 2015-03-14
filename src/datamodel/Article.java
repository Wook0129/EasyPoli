package datamodel;

public class Article {
	private String index;
	private String title;
	private String content;
	private String date;
	
	public Article(String title, String content, String date){
		this.title = title;
		this.content = content;
		this.date = date;
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
}