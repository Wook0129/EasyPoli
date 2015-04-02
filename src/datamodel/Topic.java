package datamodel;

import java.util.HashSet;
import java.util.Set;

public class Topic {
	
	private String index;
	private Set<Article> articleSet = new HashSet<Article>();
	private Article mainArticle;
	private String startDate;

	public Topic(){
	}
	public Topic(String index, Article mainArticle, Set<Article> articleSet, String startDate){
		this.index = index;
		this.mainArticle = mainArticle;
		this.articleSet = articleSet;
		this.startDate = startDate;
	}
	public String getIndex(){
		return index;
	}
	public void setIndex(int index){
		this.index = String.valueOf(index);
	}
	public Article getMainArticle(){
		return mainArticle;
	}
	public void setMainArticle(Article mainArticle){
		this.mainArticle = mainArticle;
	}
	public Set<Article> getArticles(){
		return articleSet;
	}
	public String getStartDate(){
		return startDate;
	}
	public void setStartDate(String date){
		this.startDate = date;
	}
	public void addArticle(Article article){
		articleSet.add(article);
		this.mainArticle = findMainArticle();
	}
	private Article findMainArticle(){
		Article main = new Article();
		double maxSim = 0;
		
		for(Article a : this.articleSet){
			double sim = 0;
			for(Article oa : this.articleSet){
				sim += WordVector.cosSim(a, oa);
			}
			if(sim > maxSim){
				maxSim = sim;
				main = a;
			}
		}
		return main;
	}
}