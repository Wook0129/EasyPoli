package datamodel;

import java.util.HashSet;
import java.util.Set;

public class Topic {
	private int index;
	private WordVector centralVector = new WordVector();
	private Set<StructuredArticle> articles = new HashSet<StructuredArticle>();

	public Topic(int index, WordVector centralVector, Set<StructuredArticle> articles){
		this.index = index;
		this.centralVector = centralVector;
		this.articles = articles;
	}
	public int getIndex(){
		return index;
	}
	public WordVector getCentralVector(){
		return centralVector;
	}
	public Set<StructuredArticle> getArticles(){
		return articles;
	}
	public void addArticle(StructuredArticle article){
		articles.add(article);
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