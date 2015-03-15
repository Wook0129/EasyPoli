package datamodel;

public class StructuredArticle {
	private String index;
	private WordVector termVector = new WordVector();
	private String topicNum;
	private String date;
	
	public StructuredArticle(Article article){ //기사를 크롤해서 처음 구조화시킬 때 사용
		this.termVector = new WordVector(article.getTitle() + " " + article.getContent()); //기사의 제목과 본문을 모두 어휘 벡터 생성에 이용
		this.topicNum = "0";
		this.date = article.getDate();
	}
	public StructuredArticle(String index, String jsonTermVector, String topicNum, String date){ //기사를 DB에서 불러올 때 이용
		this.index = index;
		this.termVector = WordVector.toVector(jsonTermVector);
		this.topicNum = topicNum;
		this.date = date;
	}
	public String getIndex(){
		return index;
	}
	public WordVector getTermVector(){
		return termVector;
	}
	public String getTopicNum(){
		return topicNum;
	}
	public String getDate(){
		return date;
	}
}