package main;

import java.util.HashSet;
import java.util.Set;

import database.ArticleDBHandler;
import database.TopicDBHandler;
import datacrawl.Crawler;
import datamodel.Article;
import datamodel.Topic;
import datamodel.WordVector;

public class TopicFlowFinder {

	public static void main(String[] args){

		double cutOff = 0.1;
		String startDate = "20150315";
		String endDate = "20150318";

		// 해당 기간의 기사 크롤링 후 Article Table에 저장
		Crawler c = new Crawler(startDate,endDate);
		c.crawl();

		//기사를 기존 토픽으로 분류하거나 새로운 토픽으로 만든다
		ArticleDBHandler adbh = new ArticleDBHandler();
		TopicDBHandler tdbh = new TopicDBHandler();

		Article[] articles = adbh.getAllArticles();

		for(int aCnt = 0; aCnt < articles.length; aCnt++){
			//기사에서 출현빈도가 상위권에 드는 단어만 추린 벡터
			WordVector topN = articles[aCnt].getTermVector().topNwords();

			double maxSim = 0;
			Topic toAddNewArticle = new Topic(); //새로 들어온 기사를 포함시킬 토픽
			Topic[] existTopics;

			if(aCnt == 0) { //크롤해 온 첫 기사는 곧바로 새로운 토픽으로 처리된다. 단, 이전에 누적되어있는 데이터가 없어야 함.
				//메인기사번호, 기사집합, 토픽시작날짜
				tdbh.insert(articles[aCnt].getIndex(), "\\["+articles[aCnt].getIndex()+"\\]", articles[aCnt].getDate());
				adbh.update(articles[aCnt].getIndex(), "topicnum", tdbh.lastInsertedID());
			}

			existTopics = tdbh.retrieveAllTopics();
			for(int cnt = 0; cnt<existTopics.length; cnt++){

				//메인 기사번호로, 그 기사의 어휘벡터를 가져온다
				Article mainArticle = adbh.getArticle(existTopics[cnt].getMainArticle());
				double sim = WordVector.jacqSim(topN, mainArticle.getTermVector().topNwords());
				if(sim > maxSim){
					maxSim = sim;
					//토픽의 메인 기사와, 새로운 기사가 유사하다면 새로운 기사는 토픽에 포함된다.
					toAddNewArticle = existTopics[cnt];
				}
			}

			if(maxSim > cutOff){ //Cutoff 보다 유사도가 크면, 기존 토픽에 포함시킨다(포함시키면서 중심 기사 새로 계산됨)

				toAddNewArticle.addArticle(articles[aCnt]);
				String topicNum = toAddNewArticle.getIndex();

				//토픽 DB에서 ArticleSet 업데이트, MainArticle 업데이트
				tdbh.update(topicNum, "articleset",toAddNewArticle.getArticles().toString());
				tdbh.update(topicNum, "mainArticle",toAddNewArticle.getMainArticle());
				// 기사 DB에서 Article의 인덱스로 행에 접근, topicNum을 toAddNewArticle의 인덱스로 업데이트
				adbh.update(articles[aCnt].getIndex(), "topicnum", topicNum);
			}
			else{ //Cutoff 보다 유사도가 작으면, 새로운 토픽으로 넣는다
				Set<String> articlesInTopic = new HashSet<String>();
				articlesInTopic.add(articles[aCnt].getIndex());
				//메인기사번호, 기사집합, 토픽시작날짜
				tdbh.insert(articles[aCnt].getIndex(), articlesInTopic.toString(), articles[aCnt].getDate());
				adbh.update(articles[aCnt].getIndex(), "topicnum", tdbh.lastInsertedID());
			}

		}	
	}
}
