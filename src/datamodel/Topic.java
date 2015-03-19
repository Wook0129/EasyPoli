package datamodel;

import java.util.HashSet;
import java.util.Set;

import database.StructuredArticleDBHandler;

public class Topic {
	private String index;
	private WordVector centralVector = new WordVector();
	private Set<String> articles = new HashSet<String>(); //토픽에 포함되어 있는 기사들의 인덱스
	private String startDate;

	public Topic(){

	}
	public Topic(String index, WordVector centralVector, Set<String> articles, String startDate){
		this.index = index;
		this.centralVector = centralVector;
		this.articles = articles;
		this.startDate = startDate;
	}
	public Topic(String index, String jsonCentralVector, String ArticleSetInString, String startDate){ //DB에서 꺼낼때 사용하는 생성자
		this.index = index;
		this.centralVector = WordVector.toVector(jsonCentralVector);
		if(ArticleSetInString.contains(",")){
			String[] articleArray = ArticleSetInString.replaceAll("\\[","").replaceAll("\\]", "").split(","); //기사가 여러개인 경우
			for(String article : articleArray){
				this.articles.add(article);
			}
		}else if(ArticleSetInString.contains("null")){ //기사가 아무것도 없는 경우

		}else{ //기사가 하나인 경우
			String article = ArticleSetInString.replaceAll("\\[","").replaceAll("\\]", "");
			this.articles.add(article);
		}
		this.startDate = startDate;
	}
	public String getIndex(){
		return index;
	}
	public WordVector getCentralVector(){
		return centralVector;
	}
	public Set<String> getArticles(){
		return articles;
	}
	public String getStartDate(){
		return startDate;
	}
	public void addArticle(StructuredArticle article){
		articles.add(article.getIndex());
		centralVector = findCentralVector();
	}
	private WordVector findCentralVector(){
		WordVector centralVector = new WordVector();
		double maxSim = 0;
		Object[] articleArray = articles.toArray(); //기사의 인덱스 배열임. 기사가 아니라..
		if(articleArray.length == 0) return centralVector; //Article Set이 비어있을 경우, 빈 어휘벡터 돌려줌. 그런일은 없을
		else{
			//인덱스를 바탕으로 구조화 기사들을 불러와야함
			StructuredArticleDBHandler sadbh = new StructuredArticleDBHandler();
			StructuredArticle[] sa = sadbh.getArticlesInTopic(this.index);

			for(StructuredArticle a : sa){
				if(a == null) break;
				double sim = 0;
				for(StructuredArticle oa : sa){
					if(oa == null) break;
					sim += WordVector.jacqSim(a.getTermVector().topNwords(),oa.getTermVector().topNwords());
				}
				if(sim > maxSim){
					maxSim = sim;
					centralVector = a.getTermVector();
				}
			}
			return centralVector;
		}
	}
}