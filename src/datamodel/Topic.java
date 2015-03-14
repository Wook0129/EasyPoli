package datamodel;

import java.util.HashSet;
import java.util.Set;

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
		String[] articleArray = ArticleSetInString.replaceAll("{","").replaceAll("}", "").split(",");
		for(String article : articleArray){
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
		Object[] articleArray = articles.toArray();
		if(articleArray.length == 0) return centralVector; //Article Set이 비어있을 경우, 빈 어휘벡터 돌려줌
		else{
			for(Object article : articleArray){
				StructuredArticle sa = (StructuredArticle) article;
				double sim = 0;
				for(Object otherArticle : articleArray){
					StructuredArticle oa = (StructuredArticle) otherArticle;
					sim += WordVector.jacqSim(sa.getTermVector(),oa.getTermVector());
				}
				if(sim > maxSim){
					maxSim = sim;
					centralVector = sa.getTermVector();
				}
			}
			return centralVector;
		}
	}
}