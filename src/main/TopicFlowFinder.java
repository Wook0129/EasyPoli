package main;

import java.util.HashSet;
import java.util.Set;

import database.StructuredArticleDBHandler;
import database.TopicDBHandler;
import datacrawl.Crawler;
import datamodel.StructuredArticle;
import datamodel.Topic;
import datamodel.WordVector;

public class TopicFlowFinder {
	
	public static void main(String[] args){

		double cutOff = 0.1;
		String startDate = "20150312";
		String endDate = "20150318";

		// 주어진 기간에 대한 기사 크롤링 후 Article Table에 저장, 구조화시켜서 StructruedArticle Table에 저장
		//		Crawler c = new Crawler(startDate,endDate);
		//		c.crawl();

		//오늘 들어온 기사를 기존 토픽으로 분류하거나 새로운 토픽으로 만든다
		StructuredArticleDBHandler sadbh = new StructuredArticleDBHandler();
		TopicDBHandler tdbh = new TopicDBHandler();

		StructuredArticle[] sArticles = sadbh.getAllArticles();

		for(int aCnt = 0; aCnt < sArticles.length; aCnt++){
			//sArticle에서 단어 출현빈도가 10위 안에 드는 단어만 추린 벡터
			WordVector topN = sArticles[aCnt].getTermVector().topNwords();

			double maxSim = 0;
			String topicNum = "0";
			Topic toAddNewArticle = new Topic(); //새로 들어온 기사를 포함시킬 토픽
			Topic[] existTopics;
			

			if(aCnt == 0) {
				tdbh.insert(sArticles[aCnt].getTermVector().toString(), "\\["+sArticles[aCnt].getIndex()+"\\]", sArticles[aCnt].getDate());
				sadbh.update(sArticles[aCnt].getIndex(), "topicnum", tdbh.lastInsertedID());
			}
			
			existTopics = tdbh.retrieveAllTopics();
			for(int cnt = 0; cnt<existTopics.length; cnt++){
				double sim = WordVector.jacqSim(topN, existTopics[cnt].getCentralVector().topNwords());
				if(sim > maxSim){
					maxSim = sim;
					topicNum = existTopics[cnt].getIndex();
					toAddNewArticle = existTopics[cnt];
				}
			}

			if(maxSim > cutOff){ //Cutoff 보다 유사도가 크면, 기존 토픽에 포함시킨다
				toAddNewArticle.addArticle(sArticles[aCnt]);

				//토픽 DB에서 ArticleSet 변경
				tdbh.update(topicNum, "articleset",toAddNewArticle.getArticles().toString());
				// 구조화기사 DB에서 sArticle의 인덱스로 행에 접근, topicNum을 topic의 인덱스로 바꿔야함
				sadbh.update(sArticles[aCnt].getIndex(), "topicnum", topicNum);
			}
			else{ //Cutoff 보다 유사도가 작으면, 새로운 토픽으로 넣는다
				Set<String> articlesInTopic = new HashSet<String>();
				articlesInTopic.add(sArticles[aCnt].getIndex());
				tdbh.insert(sArticles[aCnt].getTermVector().toString(), articlesInTopic.toString(), sArticles[aCnt].getDate());
				sadbh.update(sArticles[aCnt].getIndex(), "topicnum", tdbh.lastInsertedID());
			}

		}	
	}
}
