package datamodel;

public class StructuredArticle {
	private int index;
	private WordVector termVector = new WordVector();
	private String date;
	
	public StructuredArticle(Article article){
		this.index = article.getIndex();
		this.termVector = new WordVector(article.getTitle() + " " + article.getContent()); //기사의 제목과 본문을 모두 어휘 벡터 생성에 이용
		this.date = article.getDate();
	}
	public int getIndex(){
		return index;
	}
	public WordVector getTermVector(){
		return termVector;
	}
	public String getDate(){
		return date;
	}
}