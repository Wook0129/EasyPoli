package datamodel;

import java.util.HashSet;
import java.util.Set;

import database.ArticleDBHandler;

public class Topic {
	private String index;
	private String mainArticle;
	private Set<String> articleSet = new HashSet<String>(); //토픽에 포함되어 있는 기사들의 인덱스
	private String startDate;

	public Topic(){

	}
	public Topic(String index, String mainArticle, Set<String> articleSet, String startDate){
		this.index = index;
		this.mainArticle = mainArticle;
		this.articleSet = articleSet;
		this.startDate = startDate;
	}
	public Topic(String index, String mainArticle, String articleSet, String startDate){ //DB에서 꺼낼때 사용하는 생성자
		this.index = index;
		this.mainArticle = mainArticle;
		if(articleSet.contains(",")){
			String[] articleArray = articleSet.replaceAll("\\[","").replaceAll("\\]", "").split(","); //기사가 여러개인 경우
			for(String article : articleArray){
				this.articleSet.add(article);
			}
		}else if(articleSet.contains("null")){ //기사가 아무것도 없는 경우

		}else{ //기사가 하나인 경우
			String article = articleSet.replaceAll("\\[","").replaceAll("\\]", "");
			this.articleSet.add(article);
		}
		this.startDate = startDate;
	}
	public String getIndex(){
		return index;
	}
	public String getMainArticle(){
		return mainArticle;
	}
	public Set<String> getArticles(){
		return articleSet;
	}
	public String getStartDate(){
		return startDate;
	}
	public void addArticle(Article article){
		articleSet.add(article.getIndex());
		this.mainArticle = findMainArticle();
	}
	private String findMainArticle(){
		String main = "";
		double maxSim = 0;
		//토픽의 인덱스를 바탕으로 기사들을 불러온다
		ArticleDBHandler adbh = new ArticleDBHandler();
		Article[] articles = adbh.getArticlesInTopic(this.index);

		for(Article a : articles){
			if(a == null) break;
			double sim = 0;
			for(Article oa : articles){
				if(oa == null) break;
				sim += WordVector.jacqSim(a.getTermVector().topNwords(),oa.getTermVector().topNwords()); //상위 20개의 단어를 사용하여 자카드계수 계산
			}
			if(sim > maxSim){
				maxSim = sim;
				main = a.getIndex();
			}
		}
		return main;
	}
}