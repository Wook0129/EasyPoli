package main;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
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
		String startDate = "20150111";
		String endDate = "20150314";
		
		// 주어진 기간에 대한 기사 크롤링 후 Article Table에 저장, 구조화시켜서 StructruedArticle Table에 저장
		Crawler c = new Crawler(startDate,endDate);
		c.crawl();

		//오늘 들어온 기사를 기존 토픽으로 분류하거나 새로운 토픽으로 만든다
		StructuredArticleDBHandler sadbh = new StructuredArticleDBHandler();
		TopicDBHandler tdbh = new TopicDBHandler();
		
		GregorianCalendar gcal = new GregorianCalendar();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

		try { //날짜별로 30개씩 읽어와서 토픽으로 분류
			Date start = sdf.parse(startDate);
			Date end = sdf.parse(endDate);

			for(gcal.setTime(start);gcal.getTime().before(end) || gcal.getTime().equals(end);gcal.add(Calendar.DAY_OF_YEAR, 1)){
				String date = sdf.format(gcal.getTimeInMillis());

				StructuredArticle[] sArticles = sadbh.getArticlesInDate(date);

				for(StructuredArticle sArticle : sArticles){
					double maxSim = 0;
					String topicNum = "0";
					Topic toAddNewArticle = new Topic(); //새로 들어온 기사를 포함시킬 토픽

					Topic[] existTopics = tdbh.retrieveAllTopics();
					if(existTopics[0] == null) {
						topicNum = "";
						tdbh.insert(sArticle.getTermVector().toString(), "\\["+sArticle.getIndex()+"\\]", sArticle.getDate());
						sadbh.update(sArticle.getIndex(), "topicnum", tdbh.lastInsertedID());
					}
					else{

						for(Topic topic : existTopics){
							if(topic == null) break;
							double sim = WordVector.jacqSim(sArticle.getTermVector(), topic.getCentralVector());
							if(sim >= maxSim){
								maxSim = sim;
								topicNum = topic.getIndex();
								toAddNewArticle = topic;
							}
						}
						//System.out.println(maxSim);
						if(maxSim >= cutOff){ //Cutoff 보다 유사도가 크면, 기존 토픽에 포함시킨다
							toAddNewArticle.addArticle(sArticle);

							//토픽 DB에서 ArticleSet 변경
							tdbh.update(topicNum, "articleset",toAddNewArticle.getArticles().toString());
							// 구조화기사 DB에서 sArticle의 인덱스로 행에 접근, topicNum을 topic의 인덱스로 바꿔야함
							sadbh.update(sArticle.getIndex(), "topicnum", topicNum);
						}
						else{ //Cutoff 보다 유사도가 작으면, 새로운 토픽으로 넣는다
							Set<String> articlesInTopic = new HashSet<String>();
							articlesInTopic.add(sArticle.getIndex());
							tdbh.insert(sArticle.getTermVector().toString(), articlesInTopic.toString(), sArticle.getDate());
							sadbh.update(sArticle.getIndex(), "topicnum", tdbh.lastInsertedID());
						}
					}
				}	
			}
		}catch (ParseException e) {e.printStackTrace();}
	}
}
