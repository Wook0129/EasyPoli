package main;

import database.ArticleDBHandler;
import database.TopicDBHandler;
import datacrawl.Crawler;
import datamodel.Article;
import datamodel.Topic;
import datamodel.WordVector;

public class TopicFlowFinder {

	public static void main(String[] args){

		double cutOff = 0.3;

		//		String startDate = "20150101";
		//		String endDate = "20150319";
		//
		//		// 해당 기간의 기사 크롤링 후 Article Table에 저장
		//		Crawler c = new Crawler(startDate,endDate);
		//		c.crawl();

		//기사를 기존 토픽으로 분류하거나 새로운 토픽으로 만든다
		
		ArticleDBHandler adbh = new ArticleDBHandler();
		TopicDBHandler tdbh = new TopicDBHandler();
		
		System.out.println(">>>>> Getting all aritcles from database..");
		long start = System.currentTimeMillis();
		Article[] articles = adbh.getAllArticles();
		long end = System.currentTimeMillis();
		System.out.println(">>>>> Complete. Elapsed Time : "+((end - start)/(double)1000)+" sec");
		
		
		Topic[] topics = new Topic[900];
		int topicCnt = 0;

		System.out.println(">>>>>Num of Articles : "+articles.length);
		start = System.currentTimeMillis();
		for(int aCnt = 0; aCnt < articles.length; aCnt++){
			System.out.println(aCnt+" th Article");
			//기사에서 출현빈도가 상위권에 드는 단어만 추린 벡터

			double maxSim = 0;
			Topic toAddNewArticle = new Topic(); //새로 들어온 기사를 포함시킬 토픽

			if(aCnt == 0) { //Base Case. First article is directly classified as a new Topic
				topics[0] = new Topic();
				topics[0].addArticle(articles[0]);
				topics[0].setStartDate(articles[0].getDate());
				topics[0].setIndex(topicCnt);
				articles[0].setTopicNum("0");
				topicCnt++;
			}

			int cnt = 0;
			while(true){
				if(topics[cnt] == null) break;
				Article mainArticle = topics[cnt].getMainArticle();
				//기사와, 토픽의 중심기사의 유사도 계산
				//기본적으로는 Term Vector의 유사도로 비교, 인물 집합이 유사하면 유사도에 추가점을 준다
				double sim = WordVector.cosSim(articles[aCnt], mainArticle);

				if(sim > maxSim){
					maxSim = sim;
					//토픽의 메인 기사와, 새로운 기사가 유사하다면 새로운 기사는 토픽에 포함된다.
					toAddNewArticle = topics[cnt];
				}
				cnt++;
			}
//			System.out.println(" // Maximum Similarity with other topics : "+maxSim);
			if(maxSim > cutOff){ //Cutoff 보다 유사도가 크면, 기존 토픽에 포함시킨다(포함시키면서 중심 기사 새로 계산됨)
				toAddNewArticle.addArticle(articles[aCnt]);
				toAddNewArticle.setStartDate(articles[aCnt].getDate());
				articles[aCnt].setTopicNum(toAddNewArticle.getIndex());
			}
			
			else{ //Cutoff 보다 유사도가 작으면, 새로운 토픽으로 넣는다
				topics[topicCnt] = new Topic();
				topics[topicCnt].addArticle(articles[aCnt]);
				topics[topicCnt].setStartDate(articles[aCnt].getDate());
				topics[topicCnt].setIndex(topicCnt);
				articles[aCnt].setTopicNum(topics[topicCnt].getIndex());
				topicCnt++;
			}

		}
		end = System.currentTimeMillis();
		System.out.println(">>>>> Complete. Elapsed Time : "+((end - start)/(double)1000)+" sec");
		System.out.println(">>>>> Inserting result into Database..");
		for(Topic t : topics){
			if(t == null) break;
			tdbh.insert(t.getMainArticle().getIndex(), t.getArticles().toString(), t.getStartDate());
		}
		String lastIndex = tdbh.lastInsertedID();
		for(Article a : articles){
			//메모리 상의 Topic Index를 DB의 Topic Index에 맞게 변환하는 코드
			int topicIndex = Integer.parseInt(lastIndex) - topicCnt + Integer.parseInt(a.getTopicNum()) + 1;
			adbh.update(a.getIndex(), "topicnum", String.valueOf(topicIndex));
		}
		adbh.close();
		tdbh.close();
		System.out.println(">>>>> Finished!");
	}
}
