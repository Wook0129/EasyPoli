package datamodel;

public class Article {
	private int index;
	private String title;
	private String content;
	private String date;
	
	public Article(int index, String title, String content, String date){
		this.index = index;
		this.title = title;
		this.content = content;
		this.date = date;
	}
	public int getIndex(){
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